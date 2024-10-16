package com.metao.book.order.domain.service;

import com.metao.book.order.OrderCreatedEvent;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(havingValue = "dev", name = "spring.profiles.active")
public class OrderGenerator {

    private static final String ACCOUNT_ID = "ACCOUNT_ID";
    private final KafkaTemplate<String, OrderCreatedEvent> orderTemplate;
    private final AtomicInteger atomicInteger = new AtomicInteger(1);
    private final Queue<String> products = new LinkedBlockingQueue<>();

    @Value("${kafka.topic.order-created.name}")
    private String orderTopic;

    @Scheduled(fixedDelay = 30000, initialDelay = 10000)
    public void commandLineRunner() {
        var randomNumber = atomicInteger.getAndIncrement();
        var orderEvent = OrderCreatedEvent.newBuilder().setStatus(OrderCreatedEvent.Status.NEW)
            .setId(OrderCreatedEvent.UUID.getDefaultInstance().toString()).setProductId(products.poll())
            .setCustomerId(ACCOUNT_ID).setQuantity(randomNumber).setPrice(100).setCurrency("USD").build();

        orderTemplate.send(orderTopic, orderEvent.getCustomerId(), orderEvent);
    }

}
