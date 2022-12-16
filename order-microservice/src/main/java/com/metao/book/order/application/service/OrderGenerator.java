package com.metao.book.order.application.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.metao.book.order.application.dto.ProductDTO;
import com.metao.book.order.infrastructure.kafka.KafkaOrderProducer;
import com.metao.book.shared.OrderEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderGenerator {
        private static final String CUSTOMER_ID = "CUSTOMER_ID";
        private final Random random = new Random();
        private final KafkaOrderProducer kafkaProducer;
        private final FileHandler fileHandler;
        private final ProductDtoMapper mapper;
        private final AtomicInteger atomicInteger = new AtomicInteger(1);
        private List<String> productAsinList = new ArrayList<>();

        @Scheduled(fixedDelay = 10000, initialDelay = 2000)
        public void commandLineRunner() {
                var randomOrderEvent = Optional.of(atomicInteger.getAndIncrement());
                var order = randomOrderEvent.map(s -> OrderEvent
                                .newBuilder()
                                .setOrderId(s + "")
                                .setProductId(productAsinList.get(random.nextInt(productAsinList.size())))
                                .setCustomerId(CUSTOMER_ID)
                                .setStatus(Status.NEW)
                                .setQuantity(1)
                                .setPrice(100)
                                .setCurrency(Currency.dlr)
                                .setSource("PAYMENT")
                                .build());
                order.ifPresent(order -> kafkaProducer.send(orderTopic, order.getOrderId(), order));
        }

        public void loadProducts() {
                log.info("importing products data from resources");
                try (var source = fileHandler.readFromFile("data/products.txt")) {
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
