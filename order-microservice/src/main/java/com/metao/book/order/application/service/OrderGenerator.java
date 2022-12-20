package com.metao.book.order.application.service;

import com.metao.book.order.application.dto.ProductDTO;
import com.metao.book.order.infrastructure.kafka.KafkaOrderProducer;
import com.metao.book.shared.Currency;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.Status;
import com.metao.book.shared.application.service.FileHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
//@ConditionalOnProperty(havingValue = "dev", name = "spring.profiles.active")
public class OrderGenerator {

        @Value("${kafka.topic.order}")
        String orderTopic;

        private static final String CUSTOMER_ID = "CUSTOMER_ID";
        private final Random random = new Random();
        private final KafkaOrderProducer kafkaProducer;
        private final ProductDtoMapper mapper;
        private final AtomicInteger atomicInteger = new AtomicInteger(1);
        private List<String> productAsinList = new ArrayList<>();

        //@Scheduled(fixedDelay = 30000, initialDelay = 10000)
        public void commandLineRunner() {
                var randomNumber = atomicInteger.getAndIncrement();
                var order = OrderEvent.newBuilder()
                                .setOrderId(randomNumber + "")
                                .setProductId(productAsinList.get(random.nextInt(productAsinList.size())))
                                .setCustomerId(CUSTOMER_ID)
                                .setStatus(Status.NEW)
                                .setQuantity(1)
                                .setPrice(100)
                                .setCurrency(Currency.dlr)
                                .setSource("PAYMENT")
                                .build();
                kafkaProducer.send(orderTopic, order.getOrderId(), order);
        }

        public void loadProducts() {
                log.info("importing products data from resources");
            try (var source = FileHandler.readFromFile(getClass(), "data/products.txt")) {
                this.productAsinList = source
                    .map(mapper::convertToDto)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(ProductDTO::getAsin)
                    .toList();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
                log.info("finished writing to database.");
        }

        @PostConstruct
        public void afterPropertiesSet() {
                loadProducts();
        }
}
