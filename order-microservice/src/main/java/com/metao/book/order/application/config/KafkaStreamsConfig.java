package com.metao.book.order.application.config;

import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.Status;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
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

    // this builds a stream from orders then aggregates the order with reservation
    @Bean
    public KStream<String, ProductEvent> productStream(
        StreamsBuilder builder,
        NewTopic productTopic,
        SpecificAvroSerde<ProductEvent> productValuesSerdes
    ) {
        return builder.stream(productTopic.name(),
            Consumed.with(Serdes.String(), productValuesSerdes));
    }

    @Bean
    public KStream<String, OrderEvent> orderStream(
            StreamsBuilder builder,
            NewTopic orderTopic,
            SpecificAvroSerde<OrderEvent> orderValueSerdes) {
        return builder
            .stream(orderTopic.name(), Consumed.with(Serdes.String(), orderValueSerdes))
            .filter((id, order) -> order.getStatus().equals(Status.NEW));
    }

}
