package com.metao.book.product.application.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.metao.book.product.domain.exception.ProductNotFoundException;
import com.metao.book.product.domain.service.ProductService;
import com.metao.book.product.infrastructure.repository.ProductRepository;
import com.metao.book.product.util.ProductTestUtils;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepo;

    @Mock
    EntityManager entityManger;

    ProductService productService;

    @BeforeEach
    void setUp() {
        productService = Mockito.spy(new ProductService(productRepo, entityManger));
    }

    @Test
    void getProductByIdNotFound() {
        assertThrows(ProductNotFoundException.class,
            () -> productService.getProductByAsin("1"));
    }

    @Test
    void getProductByIdIsFound() {
        var pe = ProductTestUtils.createProductEntity();

        when(productRepo.findByAsin(pe.getAsin()))
            .thenReturn(Optional.of(pe));

        var product = productService.getProductByAsin(pe.getAsin());
        Assertions.assertNotNull(product);
    }

    @Test
    void testSaveProduct() {
        var pe = ProductTestUtils.createProductEntity();
        when(productService.canSaveProduct(pe)).thenReturn(true);

        productService.saveProduct(pe);

        verify(productRepo).save(eq(pe));
    }

    @Test
    void testSaveProductNotSaved() {
        var pe = ProductTestUtils.createProductEntity();
        when(productService.canSaveProduct(pe)).thenReturn(false);

        productService.saveProduct(pe);

        verify(productRepo, Mockito.never()).save(eq(pe));
    }
}
