package com.metao.book.order.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metao.book.order.application.dto.OrderDTO;
import com.metao.book.order.domain.OrderEntity;
import com.metao.book.order.domain.Status;
import com.metao.book.order.infrastructure.repository.KafkaOrderService;
import com.metao.book.order.infrastructure.repository.OrderRepository;
import com.metao.book.order.utils.BaseRedpandaIntegrationTest;
import com.metao.book.shared.domain.financial.Currency;
import com.metao.book.shared.test.TestUtils;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ActiveProfiles({"container", "test"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTest extends BaseRedpandaIntegrationTest {

    @Autowired
    KafkaOrderService orderService;

    @SpyBean
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc restTemplate;

    @Test
    public void createOrderIsOk() throws Exception {
        var order = OrderDTO.builder()
            .productId("1234567892")
            .customerId("CUSTOMER_ID")
            .status(Status.NEW)
            .currency(Currency.EUR)
            .quantity(BigDecimal.valueOf(100))
            .price(BigDecimal.valueOf(123d))
            .build();

        // post request for '/order'

        this.restTemplate.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.convertObjectToJsonBytes(objectMapper, order)))
            .andExpect(status().isOk());
        Thread.sleep(1000);
        Mockito.verify(orderRepository).save(any(OrderEntity.class));
    }

}
