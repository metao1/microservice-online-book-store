package com.metao.book.order.application.config;

import com.metao.book.order.application.dto.ProductDTO;
import com.metao.book.order.application.service.OrderJoiner;
import com.metao.book.order.domain.OrderManageService;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import com.metao.book.shared.Status;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Slf4j
@EnableKafkaStreams
@RequiredArgsConstructor
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaStreamConfig {

    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private final KafkaProperties properties;
    private final OrderManageService orderManageService;
    private final OrderJoiner orderJoiner;

    @Value("${kafka.topic.payment}")
    String paymentTopic;

    @Value("${kafka.topic.order}")
    String orderTopic;

    @Value("${kafka.topic.product}")
    String productTopic;

    @Bean
    SpecificAvroSerde<ProductEvent> productEventSpecificAvroSerde(KafkaProperties kafkaProperties) {
        var result = new SpecificAvroSerde<ProductEvent>();
        result.configure(kafkaProperties.getProperties(), false);
        return result;
    }

    @Bean
    SpecificAvroSerde<OrderEvent> OrderEventSerde() {
        var serde = new SpecificAvroSerde<OrderEvent>();
        serde.configure(properties.getProperties(), false);
        return serde;
    }

    @Bean
    public KStream<Long, OrderEvent> stream(StreamsBuilder builder, SpecificAvroSerde<OrderEvent> orderSerde) {
        KStream<Long, OrderEvent> paymentOrders = builder
            .stream(paymentTopic, Consumed.with(Serdes.Long(), orderSerde));
        KStream<Long, OrderEvent> stockOrderStream = builder
            .stream(productTopic, Consumed.with(Serdes.Long(), orderSerde));
        paymentOrders.join(
                stockOrderStream,
                orderManageService::confirm,
                JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(10)),
                StreamJoined.with(Serdes.Long(), orderSerde, orderSerde))
            .peek((k, o) -> log.info("Output: {}", o))
            .to(orderTopic);

        return paymentOrders;
    }

    @Bean
    public KTable<String, ProductDTO> productTable(
        StreamsBuilder sb, ConversionService conversionService,
        SpecificAvroSerde<ProductEvent> serde
    ) {
        return sb.table(orderTopic, Consumed.with(Serdes.String(), serde))
            .mapValues(val -> Objects.requireNonNull(conversionService.convert(val, ProductDTO.class)));
    }

    @Bean
    public KTable<String, OrderEvent> table(StreamsBuilder sb, SpecificAvroSerde<OrderEvent> specificAvroSerde) {
        var store = Stores.persistentKeyValueStore(orderTopic);
        var stream = sb.stream(orderTopic, Consumed.with(Serdes.String(), specificAvroSerde));
        return stream.toTable(Materialized.<String, OrderEvent>as(store)
            .withKeySerde(Serdes.String())
            .withValueSerde(specificAvroSerde));
    }

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
            .leftJoin(orderStream.toTable(), orderJoiner);
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
