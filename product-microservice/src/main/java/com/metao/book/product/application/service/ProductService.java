package com.metao.book.product.application.service;

import com.metao.book.product.application.exception.ProductNotFoundException;
import com.metao.book.product.domain.ProductEntity;
import com.metao.book.product.domain.ProductId;
import com.metao.book.product.domain.ProductRepository;
import com.metao.book.product.domain.ProductServiceInterface;
import com.metao.book.product.infrastructure.repository.model.OffsetBasedPageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements ProductServiceInterface {

    private final ProductRepository productRepository;

    @Override
    public Optional<ProductEntity> getProductById(ProductId productId) throws ProductNotFoundException {
        var productEntity = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("product " + productId + " not found."));
        return Optional.ofNullable(productEntity);
    }

    @Override
    public Optional<ProductEntity> getProductByAsin(String asin) throws ProductNotFoundException {
        var productEntity = productRepository.findByAsin(asin)
            .orElseThrow(() -> new ProductNotFoundException("product " + asin + " not found."));
        return Optional.ofNullable(productEntity);
    }

    @Override
    public Optional<List<ProductEntity>> getAllProductsPageable(int limit, int offset) throws ProductNotFoundException {
        var pageable = new OffsetBasedPageRequest(offset, limit);
        var pagedProducts = productRepository.findAll(pageable);
        var option = Optional.of(pagedProducts);
        return Optional.of(option.map(Page::toList))
            .orElseThrow(() -> new ProductNotFoundException("product list is empty."));
    }

    @Override
    public void saveProduct(ProductEntity pe) {
        log.info("save product:" + pe.toString());
        this.productRepository.save(pe);
    }

}
