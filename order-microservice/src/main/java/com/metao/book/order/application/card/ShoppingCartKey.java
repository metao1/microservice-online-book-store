package com.metao.book.order.application.card;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"userId", "asin"})
public class ShoppingCartKey implements Serializable {

    private String userId;

    private String asin;
}
