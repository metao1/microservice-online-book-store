package com.metao.book.order.infrastructure.kafka;

import com.metao.book.order.OrderCreatedEvent;
import com.metao.book.order.OrderPaymentEvent;
import com.metao.book.order.application.dto.exception.OrderNotFoundException;
import com.metao.book.order.application.service.OrderMapper;
import com.metao.book.order.domain.OrderId;
import com.metao.book.order.domain.OrderStatus;
import com.metao.book.order.infrastructure.repository.OrderRepository;
import com.metao.book.shared.application.service.StageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaOrderListenerConfig {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @RetryableTopic(attempts = "1")
    @KafkaListener(id = "${kafka.topic.order-created.id}",
            topics = "${kafka.topic.order-created.name}",
            groupId = "${kafka.topic.order-created.group-id}",
            containerFactory = "orderCreatedEventKafkaListenerContainerFactory")
    public void onOrderEvent(ConsumerRecord<String, OrderCreatedEvent> orderRecord) {
        StageProcessor.accept(orderRecord.value())
                .map(orderMapper::toEntity)
                .map(orderRepository::save)
                .acceptExceptionally((entity, ex) -> {
                    if (ex != null) {
                        log.error("can't save order {} , message :{}", entity, ex.getMessage());
                    } else {
                        log.error("saved order {}", entity);
                    }
                });
    }

    @RetryableTopic(attempts = "1")
    @KafkaListener(id = "${kafka.topic.order-payment.id}",
            topics = "${kafka.topic.order-payment.name}",
            groupId = "${kafka.topic.order-payment.group-id}",
            containerFactory = "orderPaymentEventKafkaListenerContainerFactory")
    public void listenToOrderPayment(ConsumerRecord<String, OrderPaymentEvent> orderRecord) {
        StageProcessor
                .accept(orderRecord.value())
                .acceptExceptionally((orderEntity, ex) -> {
                    if (ex != null) {
                        log.error("can't make order from event :{}", ex.getMessage());
                        return;
                    }
                    var foundOrder = orderRepository.findByOrderId(new OrderId(orderEntity.getId()))
                            .orElseThrow(OrderNotFoundException::new);
                    OrderStatus orderStatus = OrderStatus.valueOf(String.valueOf(orderEntity.getStatus()));
                    foundOrder.setStatus(orderStatus);
                    orderRepository.save(foundOrder);
                });
    }

}
