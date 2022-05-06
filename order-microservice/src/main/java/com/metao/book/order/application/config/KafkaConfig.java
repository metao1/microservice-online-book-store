package com.metao.book.order.application.config;

import com.metao.book.order.domain.OrderManageService;
import com.metao.book.order.infrastructure.kafka.KafkaOrderProducer;
import com.order.microservice.avro.OrderAvro;
import com.order.microservice.avro.Reservation;
import com.order.microservice.avro.Status;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.Executor;

import static org.apache.kafka.streams.kstream.Grouped.with;

@Slf4j
@Validated
@Configuration
@EnableKafkaStreams
@RequiredArgsConstructor
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaConfig {
    private static final long TIMEOUT = 10;
    private final OrderManageService orderManageService;
    private final KafkaProperties properties;

    private Random random = new Random();

    @Bean("order")
    public NewTopic orders(@Value("${kafka.stream.topic.order}") String topic) {
        return TopicBuilder
                .name(topic)
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.COMPRESSION_TYPE_CONFIG, "zstd")
                .compact()
                .build();
    }

    @Bean("order-payment")
    public NewTopic payment(@Value("${kafka.stream.topic.payment-order}") String topic) {
        return TopicBuilder
                .name(topic)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean("stock-order")
    public NewTopic stock(@Value("${kafka.stream.topic.stock-order}") String topic) {
        return TopicBuilder
                .name(topic)
                .partitions(3)
                .replicas(1)
                .compact()
                .build();
    }


    @Bean
    SpecificAvroSerde<OrderAvro> orderAvroSerde() {
        var serde = new SpecificAvroSerde<OrderAvro>();
        serde.configure(properties.getProperties(), false);
        return serde;
    }

    @Bean
    public KStream<String, OrderAvro> stream(@Value("${kafka.stream.topic.payment-order}") String paymentOrder,
                                             @Value("${kafka.stream.topic.stock-order}") String stockOrder,
                                             @Value("${kafka.stream.topic.order}") String orders,
                                             SpecificAvroSerde<OrderAvro> orderAvroSerde,
                                             StreamsBuilder sb) {

        var paymentOrderStream = sb.stream(paymentOrder, Consumed.with(Serdes.String(), orderAvroSerde));
        var stockOrderStream = sb.stream(stockOrder, Consumed.with(Serdes.String(), orderAvroSerde));

        paymentOrderStream.join(
                        stockOrderStream,
                        orderManageService::confirm,
                        JoinWindows.of(Duration.ofSeconds(TIMEOUT)),
                        StreamJoined.with(Serdes.String(), orderAvroSerde, orderAvroSerde))
                .peek((k, o) -> log.info("output :{}", o))
                .to(orders);

        return paymentOrderStream;
    }

    @Bean
    public KTable<String, OrderAvro> table(@Value("${kafka.stream.topic.order}") String orderTopic, SpecificAvroSerde<OrderAvro> specificAvroSerde, StreamsBuilder sb) {
        var store = Stores.persistentKeyValueStore(orderTopic);
        var stream = sb.stream(orderTopic, Consumed.with(Serdes.String(), specificAvroSerde));
        return stream.toTable(Materialized.<String, OrderAvro>as(store)
                .withKeySerde(Serdes.String())
                .withValueSerde(specificAvroSerde));
    }

    @Bean
    public KStream<String, OrderAvro> stream(@Value("${kafka.stream.topic.order}") String orderTopic,
                                             KafkaOrderProducer template,
                                             StreamsBuilder builder) {
        var orderSerde = new SpecificAvroSerde<OrderAvro>();
        var rsvSerde = new SpecificAvroSerde<Reservation>();
        KStream<String, OrderAvro> stream = builder
                .stream(orderTopic, Consumed.with(Serdes.String(), orderSerde))
                .peek((k, order) -> log.info("New: {}", order));

        KeyValueBytesStoreSupplier customerOrderStoreSupplier =
                Stores.persistentKeyValueStore("customer-orders");
        Aggregator<String, OrderAvro, Reservation> aggregatorService = (id, order, rsv) -> {

            if (order.getStatus().equals(Status.CONFIRM)) {
                rsv.setAmountReserved(rsv.getAmountReserved() - order.getPrice());
            } else if (order.getStatus().equals(Status.ROLLBACK)) {
                //if (!order.getSource().equals("PAYMENT")) {
                rsv.setAmountAvailable(rsv.getAmountAvailable() + order.getPrice());
                rsv.setAmountReserved(rsv.getAmountReserved() - order.getPrice());
                //}
            } else if (order.getStatus().equals(Status.NEW)) {
                if (order.getPrice() <= rsv.getAmountAvailable()) {
                    rsv.setAmountAvailable(rsv.getAmountAvailable() - order.getPrice());
                    rsv.setAmountReserved(rsv.getAmountReserved() + order.getPrice());
                    order.setStatus(Status.ACCEPT);
                } else {
                    order.setStatus(Status.REJECT);
                }
                template.send("payment-orders", order.getOrderId(), order);
            }

            log.info("{}", rsv);
            return rsv;
        };

        stream.selectKey((k, v) -> v.getCustomerId())
                .groupByKey(Grouped.with(Serdes.String(), orderSerde))
                .aggregate(
                        () -> Reservation.newBuilder().setAmountAvailable(random.nextDouble()).build(),
                        aggregatorService,
                        Materialized.<String, Reservation>as(customerOrderStoreSupplier)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(rsvSerde))
                .toStream()
                .peek((k, trx) -> log.info("Commit: {}", trx));

        return stream;
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("kafkaSender-");
        executor.initialize();
        return executor;
    }
}
