
package com.metao.book.cart.service;

import static com.metao.book.shared.kafka.Constants.TRANSACTION_MANAGER;

import com.metao.book.cart.domain.ShoppingCart;
import com.metao.book.cart.domain.ShoppingCartKey;
import com.metao.book.cart.repository.ShoppingCartRepository;
import com.metao.book.cart.service.mapper.CartMapperService;
import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.kafka.RemoteKafkaService;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(TRANSACTION_MANAGER)
@ComponentScan(basePackageClasses = RemoteKafkaService.class)
public class ShoppingCartFactory implements ShoppingCartService {

    private final RemoteKafkaService<String, OrderEvent> orderTemplate;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartMapperService.ToEventMapper cartMapper;
    private final NewTopic orderTopic;

    @Override
    public void addOrderToShoppingCart(ShoppingCart shoppingCart) {
        log.info("Adding product: " + shoppingCart.getAsin());
        ShoppingCartKey currentKey = new ShoppingCartKey(shoppingCart.getUserId(), shoppingCart.getAsin());
        Optional<ShoppingCart> shoppingCartOptional = shoppingCartRepository.findById(currentKey);
        shoppingCartOptional.ifPresent(
            sc -> shoppingCart.increaseQuantity()
        );
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public Set<ShoppingCart> getProductsInCartByUserId(String userId) {
        return shoppingCartRepository.findProductsInCartByUserId(userId);
    }

    @Override
    public void removeProductFromCart(String userId, String asin) {
        ShoppingCartKey currentKey = new ShoppingCartKey(userId, asin);
        Optional<ShoppingCart> shoppingCartOptional = shoppingCartRepository.findById(currentKey);
        shoppingCartOptional.ifPresent(shoppingCart -> {
            if (shoppingCart.getQuantity().compareTo(BigDecimal.ONE) > 0) {
                shoppingCart.decreaseQuantity();
                shoppingCartRepository.save(shoppingCart);
                log.info("Decremented product: " + asin);
            } else {
                shoppingCartRepository.delete(shoppingCart);
                log.info("Deleted product: " + asin);
            }
        });
    }

    @Override
    public int clearCart(String userId) {
        var productsInCartByUserId = shoppingCartRepository.findProductsInCartByUserId(userId);
        if (productsInCartByUserId != null) {
            shoppingCartRepository.deleteAll(productsInCartByUserId);
            log.info("Clearing all products in cart for user: {} was successful.", userId);
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public Optional<Boolean> submitProducts(String userId) {
        var products = shoppingCartRepository.findProductsInCartByUserId(userId);
        if (CollectionUtils.isEmpty(products)) {
            return Optional.empty();
        } else {
            products.stream()
                .map(cartMapper::mapToOrderEvent)
                .sorted((p1, p2) -> (int) (p1.getCreatedOn() - p2.getCreatedOn()))
                .forEachOrdered(product ->
                    orderTemplate.sendToTopic(orderTopic.name(), product.getProductId(), product));
            return Optional.of(true);
        }
    }
}
