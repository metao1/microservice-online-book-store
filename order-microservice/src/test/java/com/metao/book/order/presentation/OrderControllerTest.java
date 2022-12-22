package com.metao.book.order.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.metao.book.order.application.config.OrderStreamConfig;
import com.metao.book.order.application.config.SerdsConfig;
import com.metao.book.order.application.dto.OrderDTO;
import com.metao.book.order.application.service.OrderMapper;
import com.metao.book.order.application.service.OrderService;
import com.metao.book.order.domain.Currency;
import com.metao.book.order.domain.Status;
import com.metao.book.order.infrastructure.repository.KafkaOrderService;
import com.metao.book.order.kafka.KafkaOrderConsumerTestConfig;
import com.metao.book.order.kafka.KafkaProductConsumerConfiguration;
import com.metao.book.order.kafka.SpringBootEmbeddedKafka;

@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@ImportAutoConfiguration(classes = {
        OrderService.class,
        KafkaOrderService.class,
        OrderMapper.class,
        KafkaProductConsumerConfiguration.class,
        KafkaOrderConsumerTestConfig.class,
        SerdsConfig.class
}, exclude = { OrderStreamConfig.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTest extends SpringBootEmbeddedKafka {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void createOrder() {
        // get request for '/order'
        var order = OrderDTO.builder()
                .orderId("123")
                .productId("1234567891")
                .customerId("CUSTOMER_ID")
                .status(Status.NEW)
                .currency(Currency.EUR)
                .quantity(BigDecimal.valueOf(100))
                .price(BigDecimal.valueOf(123d))
                .build();

        var response = this.restTemplate.postForEntity("http://localhost:" + port + "/order", order, OrderDTO.class);
        assertThat(response).isNotNull();
    }

}
