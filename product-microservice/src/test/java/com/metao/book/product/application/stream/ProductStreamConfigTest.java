package com.metao.book.product.application.stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.metao.book.product.application.config.ProductStreamConfig;
import com.metao.book.product.application.service.OrderJoiner;
import com.metao.book.product.infrastructure.util.StreamsUtils;
import com.metao.book.shared.Currency;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import com.metao.book.shared.Status;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.Test;

@Slf4j
class ProductStreamConfigTest {

    private static final String PRODUCT_ID = "PRODUCT_ID", PRODUCT_ID2 = "PRODUCT_ID2";
    ProductStreamConfig kafkaStreamsConfig = new ProductStreamConfig(new OrderJoiner());

    @Test
    void productReservationStream() {
        final var orderInputTopicName = new NewTopic("order", 1, (short) 1);
        final var productInputTopicName = new NewTopic("product", 1, (short) 1);
        final var orderStockTopicName = new NewTopic("order-stock-test", 1, (short) 1);
        final var reservationTopicName = new NewTopic("order-reservation-test", 1, (short) 1);

        final var streamProps = StreamsUtils.getStreamsProperties();
        final var configMap = StreamsUtils.propertiesToMap(streamProps);

        final var sb = new StreamsBuilder();
        final var orderSerds = StreamsUtils.<OrderEvent>getSpecificAvroSerds(configMap);
        final var productSerds = StreamsUtils.<ProductEvent>getSpecificAvroSerds(configMap);
        final var reservationSerds = StreamsUtils.<ReservationEvent>getSpecificAvroSerds(configMap);

        var productStream = kafkaStreamsConfig.productStream(
            sb,
            productInputTopicName,
            productSerds
        );
        var orderStream = kafkaStreamsConfig.orderStream(
            sb,
            orderInputTopicName,
            orderSerds
        );
        var reservationTable = kafkaStreamsConfig.reservationTable(
                orderStream,
                productStream,
                orderSerds
            );
           reservationTable
               .toStream()
               .to(reservationTopicName.name());

        kafkaStreamsConfig.productReservationStream(
                reservationTable,
                orderStream
            )
            .to(orderStockTopicName.name());

        try (final TopologyTestDriver testDriver = new TopologyTestDriver(sb.build(), streamProps)) {
            var orderList = createOrderInput();
            var productList = createProductInput();
            var productInputTopic = testDriver.createInputTopic(productInputTopicName.name(),
                Serdes.String().serializer(),
                productSerds.serializer());
            var orderInputTopic = testDriver.createInputTopic(orderInputTopicName.name(),
                Serdes.String().serializer(),
                orderSerds.serializer());
            productList.forEach(product -> productInputTopic.pipeInput(product.getProductId(), product));
            orderList.forEach(order -> orderInputTopic.pipeInput(order.getProductId(), order));

            var outputTopic = testDriver.createOutputTopic(orderStockTopicName.name(),
                Serdes.String().deserializer(),
                orderSerds.deserializer());
            var reservationTopic = testDriver.createOutputTopic(reservationTopicName.name(),
                Serdes.String().deserializer(),
                reservationSerds.deserializer());
            OrderEvent expectedValues;
            while (!outputTopic.isEmpty()) {
                expectedValues = outputTopic.readValue();
                log.info("order:" + expectedValues);
                assertThat(expectedValues).isNotNull();
                //assertThat(expectedValues).isEqualTo(10);
            }
            ReservationEvent expectedReservationValues;
            while (!reservationTopic.isEmpty()) {
                expectedReservationValues = reservationTopic.readValue();
                log.info("reservation:" + expectedReservationValues);
                assertThat(expectedReservationValues).isNotNull();
                //assertThat(expectedValues).isEqualTo(10);
            }
        }
    }

    private List<ProductEvent> createProductInput() {
        List<ProductEvent> productList = new LinkedList<>();
        productList.add(ProductEvent.newBuilder()
            .setCreatedOn(Instant.now().toEpochMilli())
            .setCurrency(Currency.eur)
            .setPrice(123)
            .setVolume(100d)
            .setTitle("TITLE")
            .setProductId(PRODUCT_ID)
            .setDescription("DESCRIPTION")
            .setImageUrl("IMAGE_URL")
            .build());
        productList.add(ProductEvent.newBuilder()
            .setCreatedOn(Instant.now().toEpochMilli())
            .setCurrency(Currency.eur)
            .setPrice(200)
            .setVolume(100d)
            .setTitle("TITLE")
            .setProductId(PRODUCT_ID2)
            .setDescription("DESCRIPTION")
            .setImageUrl("IMAGE_URL")
            .build());
        return productList;
    }

    private List<OrderEvent> createOrderInput() {
        List<OrderEvent> orderList = new LinkedList<>();
        orderList.add(OrderEvent.newBuilder()
            .setProductId(PRODUCT_ID)
            .setCreatedOn(Instant.now().toEpochMilli())
            .setCurrency(Currency.eur)
            .setStatus(Status.NEW)
            .setSource("source")
            .setCustomerId("id")
            .setOrderId("1")
            .setQuantity(2)
            .setPrice(10)
            .build());
        orderList.add(OrderEvent.newBuilder()
            .setCreatedOn(Instant.now().toEpochMilli())
            .setProductId(PRODUCT_ID)
            .setStatus(Status.NEW)
            .setOrderId("2")
            .setCurrency(Currency.eur)
            .setQuantity(1)
            .setPrice(10)
            .setCustomerId("id")
            .setSource("source")
            .build());
        orderList.add(OrderEvent.newBuilder()
            .setCreatedOn(Instant.now().toEpochMilli())
            .setProductId(PRODUCT_ID2)
            .setStatus(Status.NEW)
            .setOrderId("3")
            .setCurrency(Currency.eur)
            .setQuantity(1)
            .setPrice(10)
            .setCustomerId("id")
            .setSource("source")
            .build());
        return orderList;
    }
}