package com.metao.book.cart.service;

import com.metao.book.cart.domain.ShoppingCart;
import java.util.Optional;
import java.util.Set;

public interface ShoppingCartService {

    void addProductToShoppingCart(String userId, String asin);

    Set<ShoppingCart> getProductsInCartByUserId(String userId);

    void removeProductFromCart(String userId, String asin);

    int clearCart(String userId);

    Optional<Boolean> submitProducts(String userId);
}
