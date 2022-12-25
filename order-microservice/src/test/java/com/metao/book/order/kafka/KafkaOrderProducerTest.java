package com.metao.book.order.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.metao.book.order.application.config.KafkaConfig;
import com.metao.book.order.application.config.SerdsConfig;
import com.metao.book.order.application.service.OrderMapper;
import com.metao.book.order.application.service.OrderService;
import com.metao.book.order.infrastructure.kafka.KafkaOrderProducer;
import com.metao.book.order.infrastructure.repository.KafkaOrderService;
import com.metao.book.shared.Currency;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.Status;
import com.metao.book.shared.test.TestUtils.StreamBuilder;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@Import({
        KafkaConfig.class,
        OrderService.class,
        KafkaOrderService.class,
        OrderMapper.class,
        KafkaProductConsumerConfiguration.class,
        KafkaOrderConsumerTestConfig.class,
        SerdsConfig.class
})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KafkaOrderProducerTest extends SpringBootEmbeddedKafka{

    private static final Random RAND = new Random();

    @MockBean
    KafkaOrderService orderService;

    @Autowired
    private KafkaOrderProducer kafkaProducer;

    @Autowired
    private KafkaOrderConsumerTestConfig consumer;

    @Test
    public void givenKafkaOrderTopic_whenSendingToTopic_thenMessageReceivedCorrectly() throws Exception {
        StreamBuilder.of(OrderEvent.class, 0, 10, this::createOrderFromCustomerId)
                .forEach(order -> kafkaProducer.produceOrderMessage(order));

        consumer.getLatch().await(6, TimeUnit.SECONDS);

        assertEquals(0, consumer.getLatch().getCount());
    }

    private OrderEvent createOrderFromCustomerId(Integer randomId) {
        return OrderEvent.newBuilder()
                .setOrderId("order-" + randomId)
                .setProductId("product - " + randomId)
                .setCustomerId("customer_id")
                .setCreatedOn(Instant.now().toEpochMilli())
                .setSource("PAYMENT")
                .setStatus(Status.NEW)
                .setQuantity(1)
                .setPrice(RAND.nextInt(100))
                .setCurrency(Currency.dlr)
                .build();
    }

}