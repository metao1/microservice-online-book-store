package com.metao.book.product.application.config;

import com.metao.book.product.application.service.OrderJoiner;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import com.metao.book.shared.Status;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
@ImportAutoConfiguration(value = KafkaConfig.class)
public class ProductStreamConfig {

    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private final OrderJoiner orderJoiner;

    @Bean
    public KStream<String, ProductEvent> productStream(
        StreamsBuilder builder,
        NewTopic productTopic,
        SpecificAvroSerde<ProductEvent> productValuesSerdes
    ) {
        return builder
            .stream(productTopic.name(), Consumed.with(Serdes.String(), productValuesSerdes))
            .filter((key, product) -> product.getVolume() > 0);
    }

    @Bean
    public KStream<String, OrderEvent> orderStream(
        StreamsBuilder builder,
        NewTopic orderTopic,
        SpecificAvroSerde<OrderEvent> orderValuesSerdes
    ) {
        return builder
            .stream(orderTopic.name(), Consumed.with(Serdes.String(), orderValuesSerdes))
            .filter((id, order) -> order.getStatus().equals(Status.NEW));
    }

//    reservationTable
//        .toStream()
//        .selectKey((k, v) -> v.getProductId())
//        .groupByKey(Grouped.with(Serdes.String(), reservationSerds))
//        .aggregate(() -> 0.0d,
//        (key, order, total) -> order.getReserved(),
//        Materialized.with(Serdes.String(), Serdes.Double())
//        )
//        .leftJoin(productStream.toTable(),
//                (value1, value2) -> {
//        value2.setVolume(value2.getVolume() - value1);
//        return value2;
//    }, Materialized.with(Serdes.String(), productSerds)
//        );

    @Bean
    public KStream<String, OrderEvent> productOrderStream(
        KTable<String, ReservationEvent> reservationTable,
        KStream<String, OrderEvent> orderStream
    ) {
        return reservationTable
            .toStream()
            .selectKey((id, order) -> order.getProductId())
            .leftJoin(orderStream.toTable(),
                orderJoiner);
    }

    @Bean
    public KTable<String, ReservationEvent> reservationTable(
        KStream<String, ProductEvent> productStream,
        KStream<String, OrderEvent> orderStream,
        SpecificAvroSerde<OrderEvent> orderValuesSerdes
    ) {
        return orderStream
            .selectKey((k, v) -> v.getProductId())
            .groupByKey(Grouped.with(Serdes.String(), orderValuesSerdes))
            .aggregate(() -> 0.0, (key, order, total) -> total + order.getQuantity()
                , Materialized.with(Serdes.String(), Serdes.Double()))
            .leftJoin(
                productStream.toTable(),
                (reserved, product) -> ReservationEvent.newBuilder()
                    .setCreatedOn(Instant.now().toEpochMilli())
                    .setProductId(product.getProductId())
                    .setAvailable(product.getVolume() - reserved)
                    .setReserved(reserved)
                    .setCustomerId(CUSTOMER_ID)
                    .build(),
                Materialized.as("order-reservation-state-storew"));
    }
}
