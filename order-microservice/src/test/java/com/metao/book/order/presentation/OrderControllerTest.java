package com.metao.book.order.presentation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.metao.book.order.application.config.OrderStreamConfig;
import com.metao.book.order.application.config.SerdsConfig;
import com.metao.book.order.application.dto.OrderDTO;
import com.metao.book.order.application.service.OrderService;
import com.metao.book.order.infrastructure.OrderMapperInterface;
import com.metao.book.order.infrastructure.repository.KafkaOrderService;
import com.metao.book.order.kafka.KafkaOrderConsumerTestConfig;
import com.metao.book.order.kafka.KafkaProductConsumerConfiguration;

@TestInstance(Lifecycle.PER_CLASS)
@ImportAutoConfiguration(classes = {
        OrderService.class,
        KafkaOrderService.class,
        OrderMapperInterface.class,
        KafkaProductConsumerConfiguration.class,
        KafkaOrderConsumerTestConfig.class,
        SerdsConfig.class
}, exclude = { OrderStreamConfig.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void getOrders() {
        // get request for '/order'
        var response = this.restTemplate.getForObject("http://localhost:" + port + "/order", OrderDTO.class);
        assertThat(response).isNotNull();
    }

}
