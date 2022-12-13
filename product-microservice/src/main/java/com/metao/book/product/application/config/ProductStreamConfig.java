package com.metao.book.product.application.config;

import com.metao.book.product.application.service.OrderJoiner;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import com.metao.book.shared.Status;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        return builder.stream(productTopic.name(), Consumed.with(Serdes.String(), productValuesSerdes));
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

    @Bean
    public KStream<String, OrderEvent> productReservationStream(
        KTable<String, ReservationEvent> reservationTable,
        KStream<String, OrderEvent> orderStream
    ) {
        return reservationTable
            .toStream()
            .leftJoin(
                orderStream.toTable(),
                orderJoiner
            );
    }

    @Bean
    public KTable<String, ReservationEvent> reservationTable(
        KStream<String, OrderEvent> orderStream,
        KStream<String, ProductEvent> productStream,
        SpecificAvroSerde<OrderEvent> orderValuesSerdes
    ) {
        return orderStream
            .selectKey((k, v) -> v.getProductId())
            .groupByKey(Grouped.with(Serdes.String(), orderValuesSerdes))
            .count()
            .leftJoin(
                productStream.toTable(),
                (value1, value2) -> ReservationEvent.newBuilder()
                    .setCreatedOn(Instant.now().toEpochMilli())
                    .setProductId(value2.getProductId())
                    .setAvailable(value2.getVolume() - value1)
                    .setReserved(value1.doubleValue())
                    .setCustomerId(CUSTOMER_ID)
                    .build(),
                Materialized.as("order-reservation")
            );
    }
}
