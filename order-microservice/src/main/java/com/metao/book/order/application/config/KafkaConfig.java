package com.metao.book.order.application.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.metao.book.order.application.dto.OrderDTO;
import com.metao.book.order.domain.OrderManageService;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
@EnableConfigurationProperties(value = { KafkaProperties.class })
public class KafkaConfig {

    private static final long TIMEOUT = 10;
    private final OrderManageService orderManageService;

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-app");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
                Serdes.String().getClass().getName());

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public NewTopic orders(@Value("${kafka.stream.topic.order}") String topic) {
        return TopicBuilder
                .name(topic)
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "zstd")
                .compact()
                .build();
    }

    @Bean
    public NewTopic payment(@Value("${kafka.stream.topic.payment}") String topic) {
        return TopicBuilder
                .name(topic)
                .partitions(6)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic output(@Value("${kafka.stream.topic.output}") String topic) {
        return TopicBuilder
                .name(topic)
                .partitions(6)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic stock(@Value("${kafka.stream.topic.stock}") String topic) {
        return TopicBuilder
                .name(topic)
                .partitions(6)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    public KStream<Integer, OrderDTO> stream(@Value("${kafka.stream.topic.order}") String paymentOrder,
            @Value("${kafka.stream.topic.stock}") String stockOrder,
            @Value("${kafka.stream.topic.output}") String orders,
            StreamsBuilder sb) {

        var orderJsonSerde = new JsonSerde<>(OrderDTO.class);
        var kStream = sb.stream(paymentOrder, Consumed.with(Serdes.Integer(), orderJsonSerde));

        kStream.join(
                sb.stream(stockOrder),
                orderManageService::confirm,
                JoinWindows.of(Duration.ofSeconds(TIMEOUT)),
                StreamJoined.with(Serdes.Integer(), orderJsonSerde, orderJsonSerde))
                .peek((k, o) -> log.info("output :{}", o))
                .to(orders);

        return kStream;
    }

    @Bean
    public KTable<Integer, OrderDTO> table(@Value("${kafka.stream.topic.order}") String orderTopic, StreamsBuilder sb) {
        var store = Stores.persistentKeyValueStore(orderTopic);
        var orderJsonSerde = new JsonSerde<>(OrderDTO.class);
        var stream = sb.stream(orderTopic, Consumed.with(Serdes.Integer(), orderJsonSerde));
        return stream.toTable(Materialized.<Integer, OrderDTO>as(store)
                .withKeySerde(Serdes.Integer())
                .withValueSerde(orderJsonSerde));
    }
}
