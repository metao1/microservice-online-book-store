package com.metao.book.product.application.service;

import static com.metao.book.shared.Status.ACCEPT;
import static com.metao.book.shared.Status.CONFIRM;
import static com.metao.book.shared.Status.REJECT;
import static com.metao.book.shared.Status.ROLLBACK;

import com.metao.book.product.application.exception.ProductNotFoundException;
import com.metao.book.product.domain.ProductEntity;
import com.metao.book.product.domain.ProductId;
import com.metao.book.product.domain.ProductRepository;
import com.metao.book.shared.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "prod")
public class OrderManageService {

    private static final String SOURCE = "payment";
    private final ProductRepository productRepository;

    public void reserve(OrderEvent order) {
        final ProductEntity product;
        try {
            product = productRepository.findById(new ProductId(order.getProductId())).orElseThrow(() -> new ProductNotFoundException(""));
            log.info("Found: {}", product);
            if (order.getPrice() < product.getPriceValue().doubleValue()) {
                order.setStatus(ACCEPT);
                product.setReservedItems(product.getReservedItems() + order.getQuantity());
                product.setAvailableItems(product.getAvailableItems() - order.getQuantity());
            } else {
                order.setStatus(REJECT);
            }
            order.setSource(SOURCE);
            productRepository.save(product);
            log.info("Sent: {}", order);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void confirm(OrderEvent order) {
        var product = productRepository.findById(new ProductId(order.getProductId() + "")).orElseThrow();
        log.info("Found: {}", product);
        if (order.getStatus().equals(CONFIRM)) {
            product.setReservedItems(product.getReservedItems() - order.getPrice());
            productRepository.save(product);
        } else if (order.getStatus().equals(ROLLBACK) && !order.getSource().equals(SOURCE)) {
            product.setReservedItems(product.getReservedItems() - order.getPrice());
            product.setAvailableItems(product.getAvailableItems() + order.getPrice());
            productRepository.save(product);
        }
    }
}