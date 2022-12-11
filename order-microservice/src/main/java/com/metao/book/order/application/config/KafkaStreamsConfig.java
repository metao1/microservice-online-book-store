package com.metao.book.order.application.config;

import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import com.metao.book.shared.Status;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Slf4j
@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
@ImportAutoConfiguration(value = KafkaStreamConfig.class)
public class KafkaStreamsConfig {

    private static final long TIME_WINDOW = 3; // seconds

    private static final JoinWindows joinWindow = JoinWindows.ofTimeDifferenceWithNoGrace(
        Duration.ofMinutes(TIME_WINDOW));

    // this builds a stream from orders then aggregates the order with reservation
    @Bean
    public KStream<String, ProductEvent> productStream(
        StreamsBuilder builder,
        NewTopic productTopic,
        SpecificAvroSerde<ProductEvent> productValueSerdes
    ) {
        var productStream =
            builder.stream(productTopic.name(), Consumed.with(Serdes.String(), productValueSerdes));
        return productStream;
    }

    @Bean
    public KStream<String, OrderEvent> orderStream(
        StreamsBuilder builder,
        NewTopic orderTopic,
        SpecificAvroSerde<OrderEvent> orderValueSerdes
    ) {
        var orderStream = builder
            .stream(orderTopic.name(), Consumed.with(Serdes.String(), orderValueSerdes))
            .filter((id, order) -> order.getStatus().equals(Status.NEW));
        return orderStream;
    }

    @Bean
    public KStream<String, ProductEvent> reservationStream(
        /*NewTopic reservationTopic,*/
        KStream<String, ProductEvent> productStream,
        KStream<String, OrderEvent> orderStream
    ) {
        var productTable = productStream.toTable();
        var stream = orderStream
            .groupByKey()
            //.windowedBy(SlidingWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5)))
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)).advanceBy(Duration.ofMinutes(5)))
            .aggregate(
                () -> ReservationEvent.newBuilder()
                    .setCreatedOn(Instant.now().toEpochMilli())
                    .setReserved(0d)
                    .build(),
                (key, order, reservation) -> {
                    if (Objects.equals(Status.NEW, order.getStatus())) {

                    }
                    return reservation;
                },
                Materialized.as("reserved-store")
            )
            //.suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded()))
            .toStream()
            .map((k, v) -> KeyValue.pair(k.key(), v))
            .peek((k, v) -> log.info("before-> key:{}, value:{}", k, v))
            .leftJoin(
                productTable,
                (reservation, product) -> {
                    Optional.ofNullable(product)
                        .ifPresent(productEvent -> {
                            productEvent.setVolume(productEvent.getVolume() - reservation.getReserved());
                        });
                    return product;
                }
            )
            .peek((k, v) -> log.info("after-> key:{}, value:{}", k, v));
        return stream;
    }

}
