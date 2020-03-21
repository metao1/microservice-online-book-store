package com.metao.product.retails.service;

import com.metao.product.retails.model.ProductDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {

    ProductDTO findProductById(String objectId);

    List<ProductDTO> findAllProductsPageable(int limit, int offset);

    List<ProductDTO> findAllProductsWithCategory(String category, int limit, int offset);

    void saveProduct(ProductDTO obj);
}
