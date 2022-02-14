package com.metao.product.retails.domain.product;

import com.metao.ddd.base.AbstractAggregateRoot;
import com.metao.ddd.base.DomainObjectId;
import com.metao.ddd.finance.Currency;
import com.metao.ddd.finance.Money;
import com.metao.product.retails.domain.category.CategoryEntity;
import com.metao.product.retails.domain.image.Image;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class ProductEntity extends AbstractAggregateRoot<ProductId> {

    @Min(10)
    @Max(120)
    private String title;

    @Min(90)
    @Max(1200)
    private String description;

    private Image imageUrl;

    private Double priceValue;

    private Currency priceCurrency;

    private Integer numReviews;

    private Double numStars;

    private Double avgStars;

    private ProductCategoryEntity productCategory;

    public ProductEntity(@NonNull String title,
                         @NonNull String description,
                         @NonNull Money price) {
        super(DomainObjectId.randomId(ProductId.class));
        setTitle(title);
        setPrice(price);
        setDescription(description);
        productCategory = new ProductCategoryEntity();
    }

    @SuppressWarnings("unused")
    private ProductEntity() {
        //used by JPA only
    }

    private void setDescription(@NonNull String description) {
        this.description = description;
    }

    private void setTitle(@NonNull String title) {
        this.title = title;
    }

    private void setPrice(@NonNull Money price) {
        this.priceValue = price.getAmount();
        this.priceCurrency = price.getCurrency();
    }

    public String getTitle() {
        return title;
    }

    public Double getPriceValue() {
        return priceValue;
    }

    public String getDescription() {
        return description;
    }

    public void addCategory(@NonNull CategoryEntity category) {
        productCategory.addCategoryEntity(category);
    }

    public Currency getPriceCurrency() {
        return priceCurrency;
    }

    public ProductCategoryEntity getProductCategory() {
        return productCategory;
    }

    public Double getNumStars() {
        return numStars;
    }

    public Double getAvgStars() {
        return avgStars;
    }

    public Image getImageUrl() {
        return imageUrl;
    }
}
