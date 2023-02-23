package com.metao.book.product.application.config;

import static com.metao.book.shared.kafka.Constants.KAFKA_TRANSACTION_MANAGER;

import com.metao.book.product.application.service.ProductManagerService;
import com.metao.book.product.domain.ProductRepository;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.Status;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@EnableKafka
@Profile({"!test"})
@RequiredArgsConstructor
@Transactional(KAFKA_TRANSACTION_MANAGER)
public class ProductKafkaListenerComponent {

    private final ProductManagerService productManagerService;
    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    private final CountDownLatch count = new CountDownLatch(1);

    @KafkaListener(id = "${kafka.topic.product}",
        topics = "${kafka.topic.product}",
        groupId = "${kafka.topic.product}" + "-grp")
    public void onProductEvent(ConsumerRecord<String, ProductEvent> productEvent) {
        try {
            var product = productEvent.value();
            log.info("Consumed product -> {}", product);
            var foundProduct = productRepository.findByAsin(product.getProductId());
            if (foundProduct.isEmpty()) {
                Optional.of(product)
                    .flatMap(mapper::toEntity)
                    .ifPresent(productRepository::save);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            count.countDown();
        }
    }

    @KafkaListener(id = "${kafka.topic.order}",
        topics = "${kafka.topic.order}",
        groupId = "${kafka.topic.order}" + "-grp"
    )
    public void onOrderEvent(ConsumerRecord<String, OrderEvent> orderRecord) {
        var order = orderRecord.value();
        log.info("Consumed order -> {}", order);
        if (Status.NEW.equals(order.getStatus())) {
            productManagerService.reserve(order);
        } else {
            productManagerService.confirm(order);
        }
    }

}