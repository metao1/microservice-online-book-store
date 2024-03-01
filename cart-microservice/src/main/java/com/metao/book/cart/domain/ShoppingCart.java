package com.metao.book.cart.domain;

import com.metao.book.cart.service.mapper.BaseEntity;
import com.metao.book.shared.domain.financial.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.Hibernate;

@ToString
@Entity(name = "shopping_cart")
@IdClass(ShoppingCartKey.class)
@Table(name = "shopping_cart")
@NoArgsConstructor
public class ShoppingCart implements BaseEntity {

    @Transient
    private static final int DEFAULT_QUANTITY = 1;

    @Id
    @Column(name = "user_id")
    private String userId;

    @Id
    @Column(name = "asin")
    private String asin;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "updated_time")
    private Long updatedOn;

    @Column(name = "created_time")
    private Long createdOn;

    @Column(name = "buy_price")
    private BigDecimal buyPrice;

    @Column(name = "sell_price")
    private BigDecimal sellPrice;

    @Column(name = "currency")
    private Currency currency;

    public static ShoppingCart createCart(
        String userId,
        String asin,
        BigDecimal buyPrice,
        BigDecimal sellPrice,
        BigDecimal quantity,
        Currency currency
    ) {
        return new ShoppingCart(userId, asin, buyPrice, sellPrice, quantity, currency);
    }

    public ShoppingCart(
        String userId,
        String asin,
        BigDecimal buyPrice,
        BigDecimal sellPrice,
        BigDecimal quantity,
        Currency currency
    ) {
        this.createdOn = Instant.now().toEpochMilli();
        this.asin = asin;
        this.userId = userId;
        this.quantity = quantity;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.currency = currency;
    }

    public int increaseQuantity() {
        setUpdatedOn(Instant.now().toEpochMilli());
        this.quantity = quantity.add(BigDecimal.ONE);
        return quantity.intValue();
    }

    public int decreaseQuantity() {
        if (this.quantity.compareTo(BigDecimal.ZERO) > 0) {
            this.quantity = this.quantity.subtract(BigDecimal.ONE);
        }
        setUpdatedOn(Instant.now().toEpochMilli());
        return this.quantity.intValue();
    }

    public void setAsin(String asin) {
        this.asin = asin;
        setUpdatedOn(Instant.now().toEpochMilli());
    }

    @Override
    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        setUpdatedOn(Instant.now().toEpochMilli());
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    @Override
    public Long getUpdatedOn() {
        return updatedOn;
    }

    @Override
    public void setUpdatedOn(Long updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public Long getCreatedOn() {
        return createdOn;
    }

    public String getAsin() {
        return asin;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        ShoppingCart that = (ShoppingCart) o;
        return userId != null && Objects.equals(userId, that.userId)
                && asin != null && Objects.equals(asin, that.asin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, asin);
    }

}