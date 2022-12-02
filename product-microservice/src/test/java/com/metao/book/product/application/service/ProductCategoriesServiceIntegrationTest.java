package com.metao.book.product.application.service;

import com.metao.book.product.util.BasePostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(classes = {ProductCategoriesService.class})
class ProductCategoriesServiceIntegrationTest extends BasePostgresIntegrationTest {

    @Autowired
    ProductCategoriesService categoriesService;

    @Test
    void getProductCategories() {
        categoriesService.getProductCategories("id");
    }
}