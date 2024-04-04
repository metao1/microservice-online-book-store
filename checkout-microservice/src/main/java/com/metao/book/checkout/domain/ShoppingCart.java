package com.metao.book.checkout.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@ToString
@Getter
@Setter
@Entity(name = "shopping_cart")
@IdClass(ShoppingCartKey.class)
@Table(name = "shopping_cart")
@NoArgsConstructor
public class ShoppingCart {

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

    public void increaseQuantity() {
        setUpdatedOn(Instant.now().toEpochMilli());
        this.quantity = quantity.add(BigDecimal.ONE);
    }

    public void decreaseQuantity() {
        if (this.quantity.compareTo(BigDecimal.ZERO) > 0) {
            this.quantity = this.quantity.subtract(BigDecimal.ONE);
        }
        setUpdatedOn(Instant.now().toEpochMilli());
    }

    public void setAsin(String asin) {
        this.asin = asin;
        setUpdatedOn(Instant.now().toEpochMilli());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShoppingCart that) || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        return userId != null && Objects.equals(userId, that.userId)
                && asin != null && Objects.equals(asin, that.asin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, asin);
    }

}