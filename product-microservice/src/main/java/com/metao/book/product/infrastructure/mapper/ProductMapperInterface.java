package com.metao.book.product.infrastructure.mapper;

import com.metao.book.product.application.dto.CategoryDTO;
import com.metao.book.product.application.dto.ProductDTO;
import com.metao.book.product.domain.ProductCategoryEntity;
import com.metao.book.product.domain.ProductEntity;
import com.metao.book.product.domain.category.Category;
import com.metao.book.product.domain.image.Image;
import com.metao.book.shared.Currency;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.domain.financial.Money;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Valid;
import lombok.NonNull;

public interface ProductMapperInterface {

    private static Set<ProductCategoryEntity> mapCategoryDTOsToEntities(@NonNull Set<CategoryDTO> source) {
        return source
                .stream()
                .map(CategoryDTO::getCategory)
                .map(Category::new)
                .map(ProductCategoryEntity::new)
                .collect(Collectors.toSet());
    }

    private static ProductEntity buildProductEntity(ProductDTO item) {
        var productEntity = new ProductEntity(
            item.getAsin(),
            item.getTitle(),
            item.getDescription(),
            item.getVolume(),
            new Money(item.getCurrency(), item.getPrice()),
            new Image(Optional.ofNullable(item.getImageUrl()).orElse("")));
        var categories = mapCategoryDTOsToEntities(item.getCategories());
        Stream.of(categories)
            .flatMap(Collection::stream)
            .forEach(productEntity::addCategory);
        return productEntity;
    }

    default Optional<ProductEntity> toEntity(@NonNull ProductDTO productDTO) {
        return Optional
            .of(productDTO)
            .map(ProductMapperInterface::buildProductEntity);
    }

    default List<ProductDTO> toDtos(@NonNull List<ProductEntity> allPr) {
        return allPr.stream().map(this::toDto).toList();
    }

    default Optional<ProductEntity> toEntity(ProductEvent event) {
        var categories = Arrays.stream(event.getCategories().split(",")).map(CategoryDTO::of);
        var productDto = ProductDTO.builder()
            .description(event.getDescription())
            .title(event.getTitle())
            .asin(event.getProductId())
            .volume(BigDecimal.valueOf(event.getVolume()))
            .currency(mapCurrency(event.getCurrency()))
            .price(BigDecimal.valueOf(event.getPrice()))
            .categories(categories.collect(Collectors.toSet()))
            .imageUrl(event.getImageUrl())
            .build();
        return toEntity(productDto);
    }

    default ProductDTO toDto(@Valid ProductEntity pr) {
        return ProductDTO.builder()
            .description(pr.getDescription())
            .title(pr.getTitle())
            .asin(pr.getAsin())
            .volume(pr.getVolume())
            .currency(pr.getPriceCurrency())
            .price(pr.getPriceValue())
            .categories(mapCategoryEntitiesToDTOs(pr.getCategories()))
            .imageUrl(pr.getImage().url())
            .build();
    }

    private static Set<CategoryDTO> mapCategoryEntitiesToDTOs(@NonNull Set<ProductCategoryEntity> source) {
        return source
            .stream()
            .map(ProductCategoryEntity::getCategory)
            .map(Category::category)
            .map(CategoryDTO::of)
            .collect(Collectors.toSet());
    }

    private com.metao.book.shared.domain.financial.Currency mapCurrency(Currency currency) {
        return switch (currency) {
            case dlr -> com.metao.book.shared.domain.financial.Currency.DLR;
            case eur -> com.metao.book.shared.domain.financial.Currency.EUR;
        };
    }
}
