package com.metao.book.order.application.config;

import com.metao.book.order.application.service.ProductJoiner;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
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

    private final ProductJoiner productJoiner;

    private static final JoinWindows joinWindow = JoinWindows.ofTimeDifferenceWithNoGrace(
        Duration.ofSeconds(TIME_WINDOW));

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
            .stream(orderTopic.name(), Consumed.with(Serdes.String(), orderValueSerdes));
        return orderStream;
    }

    @Bean
    public KStream<String, ReservationEvent> reservationStream(
        KStream<String, ProductEvent> productStream,
        KStream<String, OrderEvent> orderStream,
        SpecificAvroSerde<ProductEvent> productValueSerdes,
        SpecificAvroSerde<OrderEvent> orderValueSerdes
    ) {
        var streamJoined =
            StreamJoined.with(Serdes.String(), productValueSerdes, orderValueSerdes);

        var stream = productStream
            .peek((k, v) -> log.info("key:{}, value:{}", k, v))
            .selectKey((k, v) -> v.getProductId())
            .join(
                orderStream,
                productJoiner,
                joinWindow,
                streamJoined
            )
            .peek((k, v) -> log.info("key:{}, value:{}", k, v));
        return stream;
    }

}
