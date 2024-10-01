package com.metao.book.product.infrastructure.factory.handler;

import com.metao.book.product.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "kafka.isEnabled", havingValue = "true", matchIfMissing = true)
public class ProductKafkaListenerComponent {

    private final ProductDatabaseHandler productDatabaseHandler;

    @RetryableTopic(attempts = "1")
    @KafkaListener(id = "${kafka.topic.product.id}",
        topics = "${kafka.topic.product.name}",
        groupId = "${kafka.topic.product.group-id}",
        containerFactory = "productCreatedEventKafkaListenerContainerFactory"
    )
    public void onProductEvent(ConsumerRecord<String, ProductCreatedEvent> event) {
        var productCreatedEvent = event.value();
        log.debug("Consumed {}", productCreatedEvent);
        productDatabaseHandler.accept(productCreatedEvent);
    }
}
