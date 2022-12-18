package com.metao.book.product.infrastructure.util;

import java.time.Instant;

import com.metao.book.product.application.dto.ProductDTO;
import com.metao.book.product.domain.event.CreateProductEvent;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EventUtil {

    public static CreateProductEvent createEvent(ProductDTO productDTO) {
        return new CreateProductEvent(productDTO.getIsin(), productDTO, Instant.now());
    }
}
