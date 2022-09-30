package com.metao.book.retails.application.service;


import com.metao.book.shared.OrderAvro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "prod")
public class KafkaOrderProducer {

    private final KafkaTemplate<Long, OrderAvro> kafkaTemplate;

    public void send(String topic, long orderId, OrderAvro order) {
        log.info("sending order='{}' to topic='{}'", order, topic);
        kafkaTemplate.send(topic, orderId, order)
                .addCallback(result -> log.info("Sent: {}",
                                result != null ? result.getProducerRecord().value() : null),
                        ex -> {
                        });
    }
}
