package com.metao.book.order.application.config;

import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaStreamConfig {

    private final KafkaProperties properties;

    @Bean
    public SpecificAvroSerde<ProductEvent> productValuesSerdes() {
        var result = new SpecificAvroSerde<ProductEvent>();
        result.configure(properties.getProperties(), false);
        return result;
    }

    @Bean
    public SpecificAvroSerde<ReservationEvent> reservationValuesSerde() {
        var result = new SpecificAvroSerde<ReservationEvent>();
        result.configure(properties.getProperties(), false);
        return result;
    }

    @Bean
    public SpecificAvroSerde<OrderEvent> OrderEventSerde() {
        var serde = new SpecificAvroSerde<OrderEvent>();
        serde.configure(properties.getProperties(), false);
        return serde;
    }

}
