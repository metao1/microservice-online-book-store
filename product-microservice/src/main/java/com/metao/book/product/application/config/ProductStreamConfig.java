package com.metao.book.product.application.config;

import java.time.Instant;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import com.metao.book.product.application.service.OrderJoiner;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import com.metao.book.shared.Status;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
@ImportAutoConfiguration(value = KafkaConfig.class)
public class ProductStreamConfig {

    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private final OrderJoiner orderJoiner;

    private static boolean productIsValid(ProductEvent product) {
        return product != null && product.getVolume() != null && product.getVolume() > 0;
    }


    // reservationTable
    // .toStream()
    // .selectKey((k, v) -> v.getProductId())
    // .groupByKey(Grouped.with(Serdes.String(), reservationSerds))
    // .aggregate(() -> 0.0d,
    // (key, order, total) -> order.getReserved(),
    // Materialized.with(Serdes.String(), Serdes.Double())
    // )
    // .leftJoin(productStream.toTable(),
    // (value1, value2) -> {
    // value2.setVolume(value2.getVolume() - value1);
    // return value2;
    // }, Materialized.with(Serdes.String(), productSerds)
    // );

    /*
     * @Bean
     * public KStream<String, ProductEvent> productStream(
     * StreamsBuilder builder,
     * NewTopic productTopic,
     * SpecificAvroSerde<ProductEvent> productValuesSerdes
     * ) {
     * return builder
     * .stream(productTopic.name(), Consumed.with(Serdes.String(),
     * productValuesSerdes));
     * }
     */

    @Bean
    public KStream<String, OrderEvent> orderStream(
            StreamsBuilder builder,
            NewTopic orderTopic,
            SpecificAvroSerde<OrderEvent> orderSerds) {
        return builder.stream(orderTopic.name(), Consumed.with(Serdes.String(), orderSerds))
                .peek((k, order) -> log.info("order:{}", order));
    }

    @Bean
    public KStream<String, ProductEvent> productStream(
            StreamsBuilder builder,
            NewTopic productTopic,
            SpecificAvroSerde<ProductEvent> productionSerds) {
        return builder.stream(productTopic.name(), Consumed.with(Serdes.String(), productionSerds))
                .peek((k, product) -> log.info("product:{}", product));
    }

    @Bean
    public KStream<String, ReservationEvent> process(
            NewTopic reservationTopic,
            KStream<String, ProductEvent> productStream,
            KStream<String, OrderEvent> orderStream,
            SpecificAvroSerde<OrderEvent> orderSerds,
            SpecificAvroSerde<ReservationEvent> reservationSerds) {

        var stream = orderStream
                .filter((k, order) -> order.getStatus() == Status.NEW)
                .selectKey((k, v) -> v.getProductId())
                .groupByKey(Grouped.with(Serdes.String(), orderSerds))
                .aggregate(() -> 0.0, (key, order, total) -> total + order.getQuantity(),
                        Materialized.with(Serdes.String(), Serdes.Double()))
                .toStream()
                .peek((k, v) -> log.info("k:{}, v:{}", k, v))
                .join(productStream.toTable(),
                        (reserved, product) -> ReservationEvent.newBuilder()
                                .setCreatedOn(Instant.now().toEpochMilli())
                                .setProductId(product.getProductId())
                                .setAvailable((product.getVolume() == null) ? 100 : (product.getVolume() - reserved))
                                .setReserved(reserved)
                                .setCustomerId(CUSTOMER_ID)
                                .build())
                .peek((k, v) -> log.info("k:{}, v:{}", k, v));       
        stream.to(reservationTopic.name());
        return stream;
    }

}
