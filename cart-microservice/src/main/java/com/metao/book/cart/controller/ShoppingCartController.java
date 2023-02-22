package com.metao.book.cart.controller;

import com.metao.book.cart.domain.ShoppingCart;
import com.metao.book.cart.domain.dto.ShoppingCartDto;
import com.metao.book.cart.domain.dto.ShoppingCartItem;
import com.metao.book.cart.service.ShoppingCartService;
import com.metao.book.cart.service.mapper.CartMapperService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final CartMapperService cartMapper;

    @PostMapping
    public String addProductToCart(
        @RequestParam("user_id") String userId,
        @RequestParam("asin") String asin
    ) {
        shoppingCartService.addProductToShoppingCart(userId, asin);
        return asin;
    }

    @GetMapping
    public ShoppingCartDto getProductsInCart(@RequestParam("user_id") String userId) {
        final var productsInCartByUserId = shoppingCartService.getProductsInCartByUserId(userId);
        final var shoppingCartDto = cartMapper.mapToShoppingCartDto(userId, Instant.now().toEpochMilli());
        for (ShoppingCart shoppingCart : productsInCartByUserId) {
            shoppingCartDto.addItem(new ShoppingCartItem(shoppingCart.getAsin(), shoppingCart.getQuantity()));
        }
        return shoppingCartDto;
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitProducts(
        @RequestParam("user_id") String userId
    ) {
        final var submitProductsOptional = shoppingCartService.submitProducts(userId);
        if (submitProductsOptional.isPresent()) {
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(String.format("Successfully submitted for user %s", userId));
        } else {
            return ResponseEntity.unprocessableEntity()
                .body(String.format("Submitting products unsuccessful for user %s", userId));
        }
    }

    @DeleteMapping(value = "/remove")
    public String removeProductFromCart(
        @RequestParam("user_id") String userId,
        @RequestParam("asin") String asin
    ) {
        shoppingCartService.removeProductFromCart(userId, asin);
        return asin;
    }

    @PutMapping(value = "/clear")
    public ResponseEntity<String> clearCart(@RequestParam("user_id") String userId) {
        var successSignal = shoppingCartService.clearCart(userId);
        if (successSignal > 0) {
            return ResponseEntity.ok(String.format("Clearing Cart, Checkout successful for user %s", userId));
        } else {
            return ResponseEntity.unprocessableEntity()
                .body(String.format("Clearing Cart, checkout unsuccessful for user %s", userId));
        }
    }

}