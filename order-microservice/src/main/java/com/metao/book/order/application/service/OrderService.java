package com.metao.book.order.application.service;

import com.metao.book.order.domain.OrderServiceInterface;
import com.metao.book.order.infrastructure.repository.KafkaOrderService;
import com.metao.book.shared.OrderEvent;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements OrderServiceInterface {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    private final KafkaOrderService kafkaOrderService;

    @Value("${kafka.topic.order}")
    private String orderTopic;

    @Override
    public void saveOrder(OrderEvent OrderEvent) {
        kafkaTemplate.send(orderTopic, OrderEvent.getOrderId(), OrderEvent);
    }

    @Override
    public Optional<OrderEvent> getOrderByProductId(String productId) {
        return kafkaOrderService.getOrder(productId);
    }

    @Override
    public Optional<List<OrderEvent>> getAllOrdersPageable(int from, int to) {
        return Optional.empty();
    }

}
