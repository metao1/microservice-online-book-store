package com.metao.book.product.infrastructure.factory.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.metao.book.product.application.config.KafkaConfig;
import com.metao.book.product.domain.service.ProductService;
import com.metao.book.product.event.ProductCreatedEvent;
import com.metao.book.product.infrastructure.BaseKafkaIT;
import com.metao.book.product.infrastructure.factory.handler.kafka.KafkaProductConsumerTestConfig;
import com.metao.book.product.util.ProductTestUtils;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

@Import({KafkaProductConsumerTestConfig.class, KafkaConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
@TestPropertySource(properties = "kafka.isEnabled=true")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KafkaProductHandlerIT extends BaseKafkaIT {

    private static final String ASIN = "ASIN";
    @Autowired
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    @Value("${kafka.topic.product.name}")
    private String productTopic;
    @Autowired
    private KafkaProductConsumerTestConfig consumer;

    @MockBean
    private ProductService productService;

    @Test
    @SneakyThrows
    void handleGetProductEvent() {
        var event = ProductTestUtils.productCreatedEvent();
        var pe = ProductTestUtils.createProductEntity();

        when(productService.getProductByAsin(ASIN)).thenReturn(Optional.of(pe));

        kafkaTemplate.send(productTopic, event.getAsin(), event);

        consumer.getLatch().await(5, TimeUnit.SECONDS);
        assertEquals(0, consumer.getLatch().getCount());
    }

    @Test
    @SneakyThrows
    void whenSendingKafkaMessagesInParallelThenSuccessfullySentAll() {
        var event = ProductTestUtils.productCreatedEvent();

        try (var ex = Executors.newFixedThreadPool(10)) {
            // send 10 parallel kafka publish tasks and wait for them to finish
            var futures = IntStream.rangeClosed(0, 10)
                .mapToObj(i -> kafkaTemplate.send(productTopic, event.getAsin(), event)).toList();

            // wait for all futures to complete
            for (var future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        boolean completed = consumer.getLatch().await(30, TimeUnit.SECONDS);

        assertTrue(completed, "Not all messages were consumed in time");

        assertEquals(0, consumer.getLatch().getCount());
    }
}