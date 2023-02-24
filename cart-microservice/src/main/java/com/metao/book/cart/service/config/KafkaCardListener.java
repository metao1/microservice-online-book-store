package com.metao.book.cart.service.config;

import static com.metao.book.shared.kafka.Constants.KAFKA_TRANSACTION_MANAGER;

import com.metao.book.cart.service.ShoppingCartService;
import com.metao.book.shared.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
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
@ImportAutoConfiguration(value = {KafkaSerdesConfig.class})
public class KafkaCardListener {

    private final ShoppingCartService shoppingCartService;

    @Transactional(KAFKA_TRANSACTION_MANAGER)
    @KafkaListener(id = "${kafka.topic.order}",
        topics = "${kafka.topic.order}",
        groupId = "cart-order-processor-group"
    )
    void onOrder(ConsumerRecord<String, OrderEvent> record) {
        var order = record.value();
        var userId = order.getCustomerId();
        var asin = order.getProductId();
        shoppingCartService.addProductToShoppingCart(userId, asin);
    }
}
