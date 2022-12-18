package com.metao.book.order.application.config;

import java.time.Duration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import com.metao.book.order.domain.OrderManageService;
import com.metao.book.shared.OrderEvent;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
@ImportAutoConfiguration(KafkaConfig.class)
public class OrderStreamConfig {

    private final OrderManageService orderManageService;

    @Bean
    public KStream<String, OrderEvent> stream(
        StreamsBuilder builder,
        SpecificAvroSerde<OrderEvent> orderSerde,
        NewTopic paymentTopic,
        NewTopic orderTopic,
        NewTopic productTopic
    ) {
        KStream<String, OrderEvent> paymentOrders = builder
            .stream(paymentTopic.name(), Consumed.with(Serdes.String(), orderSerde));
        KStream<String, OrderEvent> stockOrderStream = builder
            .stream(productTopic.name(), Consumed.with(Serdes.String(), orderSerde));
        paymentOrders.join(
                stockOrderStream,
                orderManageService::confirm,
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(10)),
                StreamJoined.with(Serdes.String(), orderSerde, orderSerde))
            .peek((k, o) -> log.info("Output: {}", o))
            .to(orderTopic.name());

        return paymentOrders;
    }

    @Bean
    public KTable<String, OrderEvent> productTable(
        StreamsBuilder sb,
        NewTopic orderTopic,        
        SpecificAvroSerde<OrderEvent> serde
    ) {
        return sb.table(orderTopic.name(), Consumed.with(Serdes.String(), serde), Materialized.as("ORDERS"));
    }
}
