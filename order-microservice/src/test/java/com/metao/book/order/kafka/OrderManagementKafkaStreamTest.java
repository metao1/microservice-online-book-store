package com.metao.book.order.kafka;

import com.metao.book.order.application.config.KafkaStreamsConfig;
import com.metao.book.shared.Currency;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.Status;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class OrderManagementKafkaStreamTest {

    private static final String PRODUCT_ID = "PRODUCT:1";

    private final KafkaStreamsConfig kafkaStreamsConfig = new KafkaStreamsConfig();

    @Test
    void shouldAggregateReservation() {

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
