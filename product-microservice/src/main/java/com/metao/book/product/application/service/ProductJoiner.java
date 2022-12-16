package com.metao.book.product.application.service;

import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.springframework.stereotype.Component;

@Component
public class ProductJoiner implements ValueJoiner<ReservationEvent, ProductEvent,ReservationEvent> {

    /**
     * Return a joined value consisting of {@code value1} and {@code value2}.
     *
     * @param value1 the first value for joining
     * @param value2 the second value for joining
     * @return the joined value
     */
    @Override
    public ReservationEvent apply(ReservationEvent value1, ProductEvent value2) {
        return null;
    }
}
