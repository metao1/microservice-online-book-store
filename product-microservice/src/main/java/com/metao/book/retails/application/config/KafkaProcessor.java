package com.metao.book.retails.application.config;

import com.metao.book.retails.application.service.OrderManageService;
import com.order.microservice.avro.OrderAvro;
import com.order.microservice.avro.Reservation;
import com.order.microservice.avro.Status;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Slf4j
@Component
@Validated
@EnableKafka
@RequiredArgsConstructor
public class KafkaProcessor {
    private static final String STOCK = "STOCK";
    private final OrderManageService orderManager;
    private final KafkaTemplate<Long, OrderAvro> template;

    @Value("${spring.kafka.properties.schema.registry.url}")
    String srUrl;

    @Value("${spring.kafka.properties.basic.auth.credentials.source}")
    String crSource;

    @Value("${spring.kafka.properties.schema.registry.basic.auth.user.info}")
    String authUser;

    @Bean
    SpecificAvroSerde<OrderAvro> orderAvroSerde() {
        SpecificAvroSerde<OrderAvro> serde = new SpecificAvroSerde<>();
        final Map<String, String>
                config =
                Map.of(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, srUrl,
                        "basic.auth.credentials.source", crSource,
                        AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, authUser);
        serde.configure(config, false);
        return serde;
    }

    @Bean
    SpecificAvroSerde<Reservation> reservationAvroSerde() {
        SpecificAvroSerde<Reservation> serde = new SpecificAvroSerde<>();
        final Map<String, String>
                config =
                Map.of(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, srUrl,
                        "basic.auth.credentials.source", crSource,
                        AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, authUser);
        serde.configure(config, false);
        return serde;
    }

    @KafkaListener(id = "orders", topics = "order-test-3", groupId = "order-group")
    public void onEvent(ConsumerRecord<Long, OrderAvro> record) {
        log.info("Consumed message -> {}", record.value());
        var order = record.value();
        try {
            if (order.getStatus().equals(Status.NEW)) {
                orderManager.reserve(order);
            } else {
                orderManager.confirm(order);
            }
        } catch (Exception e) {
            log.error("Error processing order", e);
        }
    }

    // this builds a stream from orders then aggregates the order with reservation
    //@Bean
//    public KStream<Long, OrderAvro> stream(StreamsBuilder builder,
//                                           ProductService productService,
//                                           SpecificAvroSerde<OrderAvro> orderAvroSerde,
//                                           SpecificAvroSerde<Reservation> rsvSerde) {
//
//        KStream<Long, OrderAvro> stream = builder
//                .stream(orderKafkaTopic, Consumed.with(Serdes.Long(), orderAvroSerde));
//
//        KeyValueBytesStoreSupplier stockOrderStoreSupplier =
//                Stores.persistentKeyValueStore(orderKafkaTopic);
//
//        Aggregator<Long, OrderAvro, Reservation> aggrSrv = (id, order, rsv) -> {
//            switch (order.getStatus()) {
//                case CONFIRM -> rsv.setAmountReserved(rsv.getAmountReserved() - order.getQuantity());
//                case ROLLBACK -> {
//                    if (!order.getSource().equals(STOCK)) {
//                        rsv.setAmountAvailable(rsv.getAmountAvailable() + order.getQuantity());
//                        rsv.setAmountReserved(rsv.getAmountReserved() - order.getQuantity());
//                    }
//                }
//                case NEW -> {
//                    if (order.getQuantity() <= rsv.getAmountAvailable()) {
//                        rsv.setAmountAvailable(rsv.getAmountAvailable() - order.getQuantity());
//                        rsv.setAmountReserved(rsv.getAmountReserved() + order.getQuantity());
//                        order.setStatus(ACCEPT);
//                    } else {
//                        order.setStatus(REJECT);
//                    }
//                    template.send(paymentKafkaTopic, order.getOrderId(), order)
//                            .addCallback(result -> log.info("Sent: {}",
//                                    result != null ? result.getProducerRecord().value() : null), ex -> {});
//                }
//            }
//            log.info("{}", rsv);
//            return rsv;
//        };
//
//        stream.selectKey((k, v) -> v.getProductId())
//                .groupByKey(Grouped.with(Serdes.Long(), orderAvroSerde))
//                .aggregate(() -> Reservation.newBuilder()
//                                .setAmountAvailable(productService.countProducts())
//                                .setAmountReserved(0)
//                                .build(),
//                        aggrSrv,
//                        Materialized.<Long, Reservation>as(stockOrderStoreSupplier)
//                                .withKeySerde(Serdes.Long())
//                                .withValueSerde(rsvSerde))
//                .toStream()
//                .peek((k, trx) -> log.info("Commit: {}", trx));
//
//        return stream;
//    }

}
