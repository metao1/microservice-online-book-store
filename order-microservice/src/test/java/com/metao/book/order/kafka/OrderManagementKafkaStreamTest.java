package com.metao.book.order.kafka;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.metao.book.order.application.config.KafkaStreamsConfig;
import com.metao.book.order.utils.StreamsUtils;
import com.metao.book.shared.Currency;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
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
public class OrderManagementKafkaStreamTest {

    private static final String PRODUCT_ID = "PRODUCT:1";

    private final KafkaStreamsConfig kafkaStreamsConfig = new KafkaStreamsConfig();

    @Test
    void shouldAggregateReservation() {

        final var orderInputTopicName = new NewTopic("order", 1, (short) 1);
        final var productInputTopicName = new NewTopic("product", 1, (short) 1);
        final var outputTopicName = new NewTopic("reservation", 1, (short) 1);

        final var streamProps = StreamsUtils.getStreamsProperties();
        final var configMap = StreamsUtils.propertiesToMap(streamProps);

        final var sb = new StreamsBuilder();
        final var orderSerds = StreamsUtils.<OrderEvent>getSpecificAvroSerds(configMap);
        final var productSerds = StreamsUtils.<ProductEvent>getSpecificAvroSerds(configMap);
        final var productStream = kafkaStreamsConfig.productStream(sb, productInputTopicName, productSerds);
        final var orderStream = kafkaStreamsConfig.orderStream(sb, orderInputTopicName, orderSerds);

        kafkaStreamsConfig.reservationStream(
                productStream,
                orderStream
            )
            .to(outputTopicName.name());

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

            var outputTopic = testDriver.createOutputTopic(outputTopicName.name(),
                Serdes.String().deserializer(),
                productSerds.deserializer());

            var expectedValues = outputTopic.readValue();
            assertThat(expectedValues).isNotNull();
            assertThat(expectedValues).isEqualTo(10);
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
            .setQuantity(1)
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
        return orderList;
    }
}