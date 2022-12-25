package com.metao.book.product.application.config;

import com.metao.book.product.application.service.OrderAggregator;
import com.metao.book.product.application.service.OrderProductJoiner;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import com.metao.book.shared.Status;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.Stores;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Slf4j
@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
@Profile({"!test", "!container"})
@ImportAutoConfiguration(value = KafkaConfig.class)
public class ProductStreamConfig {

        private final static String ORDER_RESERVATION_STORE_NAME = "order-reservation";

        private final OrderProductJoiner orderProductJoiner;
        private final OrderAggregator orderAggregator;

        @Bean
        public KStream<String, ReservationEvent> reservationStream(
                        StreamsBuilder builder,
                        NewTopic reservationTopic,
                        NewTopic productTopic,
                        NewTopic orderTopic,
                        SpecificAvroSerde<ProductEvent> productSerds,
                        SpecificAvroSerde<OrderEvent> orderSerds,
                        SpecificAvroSerde<ReservationEvent> reservationSerds) {

                var orderStream = builder.stream(orderTopic.name(), Consumed.with(Serdes.String(), orderSerds))
                                .peek((k, order) -> log.info("order:{}", order));
                var productStream = builder.stream(productTopic.name(), Consumed.with(Serdes.String(), productSerds))
                                .peek((k, product) -> log.info("product:{}", product));

                var customerOrderStoreSupplier = Stores.persistentKeyValueStore(ORDER_RESERVATION_STORE_NAME);

                var reservationStateStore = Materialized.<String, Double>as(customerOrderStoreSupplier)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.Double());

                var stream = orderStream
                                .filter((k, order) -> order.getStatus() == Status.NEW)
                                .selectKey((k, v) -> v.getProductId())
                                .groupByKey(Grouped.with(Serdes.String(), orderSerds))
                                .aggregate(() -> 0.0d, orderAggregator, reservationStateStore)
                                .toStream()
                                .join(productStream.toTable(), orderProductJoiner)
                                .peek((k, v) -> log.info("k:{}, v:{}", k, v));
                stream.to(reservationTopic.name(), Produced.with(Serdes.String(), reservationSerds));
                return stream;
        }

        @Bean
        public Topology topology(StreamsBuilder streamsBuilder, SpecificAvroSerde<ProductEvent> productSerde) {
                streamsBuilder.addStateStore(
                                Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore("product-state-store"),
                                                Serdes.String(),
                                                productSerde));
                Topology topology = streamsBuilder.build();
                return topology;
        }

}
