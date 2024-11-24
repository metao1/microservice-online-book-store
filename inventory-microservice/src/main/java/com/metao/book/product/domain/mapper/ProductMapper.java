package com.metao.book.product.domain.mapper;

import com.google.protobuf.Timestamp;
import com.metao.book.product.domain.ProductEntity;
import com.metao.book.product.domain.category.ProductCategoryEntity;
import com.metao.book.product.domain.category.dto.CategoryDTO;
import com.metao.book.product.domain.dto.ProductDTO;
import com.metao.book.product.event.Category;
import com.metao.book.product.event.ProductCreatedEvent;
import com.metao.book.product.event.ProductUpdatedEvent;
import com.metao.book.shared.domain.financial.Money;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductMapper {

    public static ProductDTO toDto(@NotNull ProductEntity pr) throws NullPointerException {
        return ProductDTO.builder()
            .description(pr.getDescription())
            .title(pr.getTitle())
            .asin(pr.getAsin())
            .volume(pr.getVolume())
            .currency(pr.getPriceCurrency())
            .price(pr.getPriceValue())
            .boughtTogether(
                Stream.ofNullable(pr.getBoughtTogether()).flatMap(s -> Arrays.stream(s.split(","))).toList())
            .categories(mapCategoryEntitiesToDTOs(pr.getCategories()))
            .imageUrl(pr.getImageUrl())
            .build();
    }

    public static ProductCreatedEvent toProductCreatedEvent(ProductDTO pr) throws NullPointerException {
        return ProductCreatedEvent.newBuilder()
            .setAsin(pr.asin())
            .setTitle(pr.title())
            .setDescription(pr.description())
            .setPrice(pr.price().doubleValue())
            .setCurrency(pr.currency().toString())
            .setImageUrl(pr.imageUrl())
            .setVolume(pr.volume().doubleValue())
            .setCreateTime(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
            .addAllBoughtTogether(pr.boughtTogether() != null ? pr.boughtTogether() : List.of())
            .addAllCategories(mapToCategories(pr))
            .build();
    }

    public static ProductEntity fromDto(@NonNull ProductDTO pr) {
        var pe = new ProductEntity(pr.asin(),
            pr.title(),
            pr.description(),
            pr.volume(),
            new Money(pr.currency(), pr.price()),
            Optional.of(pr.imageUrl()).orElse(""));

        pe.setBoughtTogether(pr.boughtTogether());
        pe.addCategories(mapCategoryDTOsToEntities(mapToCategories(pr)));
        return pe;
    }

    public static ProductEntity fromProductCreatedEvent(@NonNull ProductCreatedEvent event) {
        var pe = new ProductEntity(event.getAsin(), event.getTitle(), event.getDescription(),
            BigDecimal.valueOf(event.getVolume()),
            new Money(Currency.getInstance(event.getCurrency()), BigDecimal.valueOf(event.getPrice())),
            Optional.of(event.getImageUrl()).orElse(""));

        pe.setBoughtTogether(event.getBoughtTogetherList());
        pe.addCategories(mapCategoryDTOsToEntities(event.getCategoriesList()));
        return pe;
    }

    public static ProductEntity fromProductUpdatedEvent(@NonNull ProductUpdatedEvent event) {
        var pe = new ProductEntity(event.getAsin(), event.getTitle(), event.getDescription(),
            BigDecimal.valueOf(event.getVolume()),
            new Money(Currency.getInstance(event.getCurrency()), BigDecimal.valueOf(event.getPrice())),
            Optional.of(event.getImageUrl()).orElse(""));

        pe.setUpdateTime(LocalDateTime.from(Instant.ofEpochSecond(event.getUpdatedTime().getSeconds())));
        pe.addCategories(mapCategoryDTOsToEntities(event.getCategoriesList()));
        return pe;
    }

    private static List<Category> mapToCategories(ProductDTO pr) {
        return pr.categories() != null ?
            pr.categories().stream().map(dto -> Category.newBuilder().setName(dto.category()).build())
                .toList() : List.of();
    }

    private static Set<ProductCategoryEntity> mapCategoryDTOsToEntities(@NonNull List<Category> categories) {
        return categories.stream()
            .map(Category::getName)
            .map(ProductCategoryEntity::new)
            .collect(Collectors.toSet());
    }

    private static Set<CategoryDTO> mapCategoryEntitiesToDTOs(@NonNull Set<ProductCategoryEntity> source) {
        return source.stream()
            .map(ProductCategoryEntity::getCategory)
            .map(CategoryDTO::new)
            .collect(Collectors.toSet());
    }

}
