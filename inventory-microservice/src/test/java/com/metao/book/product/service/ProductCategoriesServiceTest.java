package com.metao.book.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.metao.book.product.domain.category.ProductCategoryEntity;
import com.metao.book.product.domain.service.ProductCategoriesService;
import com.metao.book.product.infrastructure.repository.ProductRepository;
import com.metao.book.product.util.ProductTestUtils;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductCategoriesServiceTest {

    public static final String PRODUCT_ID = "id";
    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductCategoriesService productCategoriesService;

    @Test
    void getProductCategories() {
        var returnedProductCategories = Optional.of(ProductTestUtils.createProductEntity());
        doReturn(returnedProductCategories)
            .when(productRepository)
            .findByAsin(PRODUCT_ID);

        var categories = productCategoriesService.getProductCategories(PRODUCT_ID);

        assertThat(categories)
            .extracting(ProductCategoryEntity::getCategory)
            .contains("category");

    }
}
