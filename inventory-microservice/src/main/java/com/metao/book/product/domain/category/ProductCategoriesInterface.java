package com.metao.book.product.domain.category;

import java.util.Set;

public interface ProductCategoriesInterface {

    Set<ProductCategoryEntity> getProductCategories(String productId);
}
