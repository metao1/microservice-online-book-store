package com.metao.book.order.application.service;

import java.util.List;
import java.util.Optional;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Service;

import com.metao.book.order.domain.OrderServiceInterface;
import com.metao.book.order.infrastructure.kafka.KafkaOrderProducer;
import com.metao.book.order.infrastructure.repository.KafkaOrderService;
import com.metao.book.shared.OrderEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderServiceInterface {

    private final KafkaOrderService kafkaOrderService;
    private final KafkaOrderProducer kafkaOrderProducer;
    private final NewTopic orderTopic;

    @Override
    public void saveOrder(OrderEvent orderEvent) {
        kafkaOrderProducer.send(orderTopic.name(), orderEvent.getOrderId(), orderEvent);
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
