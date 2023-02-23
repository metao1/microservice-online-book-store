package com.metao.book.order.infrastructure.kafka;

import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.kafka.RemoteKafkaService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = RemoteKafkaService.class)
public class KafkaOrderProducer {

    private final RemoteKafkaService<String, OrderEvent> kafkaTemplate;
    private final NewTopic orderTopic;

    public void sendToKafka(OrderEvent orderEvent) {
        sendToKafka(orderTopic.name(), orderEvent);
    }

    public void sendToKafka(String topic, OrderEvent orderEvent) {
        kafkaTemplate.sendToTopic(topic, orderEvent.getProductId(), orderEvent);
    }

}
