package com.metao.book.order.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebMvcTest(controllers = OrderController.class)
@Import(value = {})
public class OrderControllerTest {

        @Autowired
        WebTestClient client;

        @Test
        public void getOrders() {
                client.get()
                        .uri("/order")
                        .exchange()
                        .expectStatus()
                        .isOk();                
        }

}
