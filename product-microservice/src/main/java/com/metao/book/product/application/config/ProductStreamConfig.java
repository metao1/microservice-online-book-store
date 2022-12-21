package com.metao.book.product.application.config;

import java.time.Instant;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
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

        @Bean
        public KStream<String, ReservationEvent> process(
                        StreamsBuilder builder,
                        NewTopic reservationTopic,
                        NewTopic productTopic,
                        NewTopic orderTopic,
                        SpecificAvroSerde<ProductEvent> productionSerds,
                        SpecificAvroSerde<OrderEvent> orderSerds,
                        SpecificAvroSerde<ReservationEvent> reservationSerds) {

                var orderStream = builder.stream(orderTopic.name(), Consumed.with(Serdes.String(), orderSerds))
                                .peek((k, order) -> log.info("order:{}", order));
                var productStream = builder.stream(productTopic.name(), Consumed.with(Serdes.String(), productionSerds))
                                .peek((k, product) -> log.info("product:{}", product));

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
                                                                .setAvailable((product.getVolume() == null) ? 100
                                                                                : (product.getVolume() - reserved))
                                                                .setReserved(reserved)
                                                                .setCustomerId(CUSTOMER_ID)
                                                                .build())
                                .peek((k, v) -> log.info("k:{}, v:{}", k, v));
                stream.to(reservationTopic.name(), Produced.with(Serdes.String(), reservationSerds));
                return stream;
        }

}
