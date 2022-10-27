package com.metao.book.cart.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.metao.book.cart.domain.ShoppingCart;
import com.metao.book.cart.domain.ShoppingCartKey;
import com.metao.book.cart.repository.ShoppingCartRepository;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.ThrowingConsumer;

@TestInstance(Lifecycle.PER_CLASS)
class ShoppingCartServiceTest {

    ShoppingCartRepository shoppingCartRepository = spy(ShoppingCartRepository.class);

    private final ShoppingCartService shoppingCartService = new ShoppingCartCartFactory(shoppingCartRepository);

    @TestFactory
    Stream<DynamicTest> addProductToShoppingCart() {
        // Generates display names like: input:5, input:37, input:85, etc.
        Function<ShoppingCart, String> displayNameGenerator = (input) -> "input:" + input;

        ThrowingConsumer<ShoppingCart> testExecutor = (shoppingCart) -> {
            shoppingCartService.addProductToShoppingCart(shoppingCart.getUserId(), shoppingCart.getAsin());
            assertFalse(shoppingCartService.getProductsInCartByUserId(shoppingCart.getUserId()).isEmpty());
            verify(shoppingCartRepository).save(shoppingCart);
        };
        Stream<ShoppingCart> of = StreamBuilder.of(ShoppingCart.class, 1, 20);
        return DynamicTest.stream(of, displayNameGenerator, testExecutor);
    }

    private static class StreamBuilder {

        static <R extends ShoppingCart> Stream<R> of(Class<R> clazz, int low, int range) {
            return IntStream.range(low, range)
                .boxed()
                .map(i -> ShoppingCart.createCart(new ShoppingCartKey("user_id_" + i, i.toString())))
                .map(clazz::cast);
        }
    }
}