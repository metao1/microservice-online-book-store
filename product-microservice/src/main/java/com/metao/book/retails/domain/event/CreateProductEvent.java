package com.metao.book.retails.domain.event;

import com.metao.book.retails.application.dto.ProductDTO;
import com.metao.book.shared.domain.base.DomainEvent;
import org.springframework.lang.NonNull;

import java.time.Instant;

public class CreateProductEvent implements DomainEvent {
    private final ProductDTO productDTO;
    private final Instant createdOn;
    private final Instant occurredOn;

    public CreateProductEvent(
            @NonNull final ProductDTO productDTO,
            @NonNull final Instant createdOn,
            @NonNull final Instant occurredOn) {
        this.occurredOn = occurredOn;
        this.createdOn = createdOn;
        this.productDTO = productDTO;
    }

    @NonNull
    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    public Instant createdOn() {
        return createdOn;
    }

    public ProductDTO productDTO() {
        return productDTO;
    }
}
