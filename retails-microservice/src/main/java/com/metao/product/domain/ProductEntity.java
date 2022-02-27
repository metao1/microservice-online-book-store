package com.metao.product.domain;

import com.metao.ddd.base.AbstractAggregateRoot;
import com.metao.ddd.base.DomainObjectId;
import com.metao.ddd.finance.Currency;
import com.metao.ddd.finance.Money;
import com.metao.product.domain.category.CategoryEntity;
import com.metao.product.domain.image.Image;
import org.springframework.lang.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class ProductEntity extends AbstractAggregateRoot<ProductId> {

    @Min(10)
    @Max(120)
    private String title;

    @Min(90)
    @Max(1200)
    private String description;

    @NonNull
    private Image image;

    @NonNull
    private Double priceValue;

    @Valid
    private Currency priceCurrency;

    private ProductCategoryEntity productCategory;

    public ProductEntity(@NonNull String title,
                         @NonNull String description,
                         @NonNull Money money,
                         @NonNull Image image) {
        super(DomainObjectId.randomId(ProductId.class));
        this.title = title;
        this.description = description;
        this.priceValue = money.getAmount();
        this.priceCurrency = money.getCurrency();        
        this.image= image;        
        productCategory = new ProductCategoryEntity();
    }

    @SuppressWarnings("unused")
    private ProductEntity() {
        
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

    public Image getImage() {
        return image;
    }
}