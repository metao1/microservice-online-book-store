package com.metao.book.order.application.config;

import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaStreamConfig {

    private final KafkaProperties properties;

    @Bean
    SpecificAvroSerde<ProductEvent> productEventSpecificAvroSerde(KafkaProperties kafkaProperties) {
        var result = new SpecificAvroSerde<ProductEvent>();
        result.configure(kafkaProperties.getProperties(), false);
        return result;
    }

    @Bean
    SpecificAvroSerde<ReservationEvent> reservationEventSpecificAvroSerde(KafkaProperties kafkaProperties) {
        var result = new SpecificAvroSerde<ReservationEvent>();
        result.configure(kafkaProperties.getProperties(), false);
        return result;
    }

    @Bean
    SpecificAvroSerde<OrderEvent> OrderEventSerde() {
        var serde = new SpecificAvroSerde<OrderEvent>();
        serde.configure(properties.getProperties(), false);
        return serde;
    }

}
