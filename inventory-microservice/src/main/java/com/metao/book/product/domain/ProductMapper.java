package com.metao.book.product.domain;

import com.google.protobuf.Timestamp;
import com.metao.book.product.application.dto.CategoryDTO;
import com.metao.book.product.application.dto.ProductDTO;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.metao.book.product.domain.image.Image;
import com.metao.book.product.event.Category;
import com.metao.book.product.event.ProductCreatedEvent;
import com.metao.book.shared.domain.financial.Money;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public List<ProductDTO> toDTOList(@NonNull List<ProductEntity> allPr) {
        return allPr.stream().map(this::toDto).toList();
    }

    public ProductDTO toDto(ProductEntity pr) {
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

    public ProductCreatedEvent toEvent(ProductDTO pr) {
        try {
            return ProductCreatedEvent.newBuilder()
                .setAsin(pr.asin())
                .setTitle(pr.title())
                .setDescription(pr.description() == null ? "" : pr.description())
                .setPrice(pr.price().doubleValue())
                .setCurrency(pr.currency().toString())
                .setImageUrl(pr.imageUrl() == null ? "" : pr.imageUrl())
                .setVolume(pr.volume().doubleValue())
                .setCreateTime(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
                .addAllCategories(
                    pr.categories().stream().map(dto -> Category.newBuilder().setName(dto.getCategory()).build())
                        .toList()
                ).build();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public ProductEntity toEntity(@NonNull ProductCreatedEvent event) {
        var productEntity = new ProductEntity(
            event.getAsin(),
            event.getTitle(),
            event.getDescription(),
            BigDecimal.valueOf(event.getVolume()),
            new Money(Currency.getInstance(event.getCurrency()), BigDecimal.valueOf(event.getPrice())),
            new Image(Optional.of(event.getImageUrl()).orElse("")));
        var categories = mapCategoryDTOsToEntities(event.getCategoriesList());
        Stream.of(categories)
            .flatMap(Collection::stream)
            .forEach(productEntity::addCategory);
        return productEntity;
    }

    private static Set<ProductCategoryEntity> mapCategoryDTOsToEntities(@NonNull List<Category> categories) {
        return categories
            .stream()
            .map(Category::getName)
            .map(com.metao.book.product.domain.category.Category::new)
            .map(ProductCategoryEntity::new)
            .collect(Collectors.toSet());
    }

    private static Set<CategoryDTO> mapCategoryEntitiesToDTOs(@NonNull Set<ProductCategoryEntity> source) {
        return source
            .stream()
            .map(ProductCategoryEntity::getCategory)
            .map(com.metao.book.product.domain.category.Category::getValue)
            .map(CategoryDTO::new)
            .collect(Collectors.toSet());
    }

}
