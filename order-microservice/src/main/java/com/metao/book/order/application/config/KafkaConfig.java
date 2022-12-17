package com.metao.book.order.application.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.validation.annotation.Validated;

import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;

@Validated
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaConfig {

    @Bean
    public NewTopic orderTopic(@Value("${kafka.topic.order}") String topic) {
        return TopicBuilder
                .name(topic)
                .partitions(1)
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "zstd")
                .compact()
                .build();
    }

    @Bean
    public NewTopic reservationTopic(@Value("${kafka.topic.reservation}") String topic) {
        return TopicBuilder
                .name(topic)
                .partitions(1)
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "zstd")
                .compact()
                .build();
    }

    @Bean
    public NewTopic paymentTopic(@Value("${kafka.topic.payment}") String topic) {
        return TopicBuilder
            .name(topic)
            .partitions(1)
            .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "zstd")
            .compact()
            .build();
    }

    @Bean
    public NewTopic productTopic(@Value("${kafka.topic.product}") String topic) {
        return TopicBuilder
            .name(topic)
            .partitions(1)
            .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "zstd")
            .compact()
            .build();
    }

    @Bean
    SpecificAvroSerde<ProductEvent> productEventSerde(KafkaProperties kafkaProperties) {
        var result = new SpecificAvroSerde<ProductEvent>();
        result.configure(kafkaProperties.getProperties(), false);
        return result;
    }

    @Bean
    SpecificAvroSerde<OrderEvent> orderEventSerde(KafkaProperties kafkaProperties) {
        var serde = new SpecificAvroSerde<OrderEvent>();
        serde.configure(kafkaProperties.getProperties(), false);
        return serde;
    }

}
