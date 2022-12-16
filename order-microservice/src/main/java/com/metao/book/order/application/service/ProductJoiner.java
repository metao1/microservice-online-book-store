package com.metao.book.order.application.service;

import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ProductEvent;
import com.metao.book.shared.ReservationEvent;
import java.time.Instant;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.springframework.stereotype.Component;

@Component
public class ProductJoiner implements ValueJoiner<ProductEvent, OrderEvent, ReservationEvent> {

    /**
     * Return a joined value consisting of {@code value1} and {@code value2}.
     *
     * @param productEvent the first value for joining
     * @param orderEvent   the second value for joining
     * @return the joined value
     */
    public ReservationEvent apply(ProductEvent productEvent, OrderEvent orderEvent) {
        return ReservationEvent.newBuilder()
            .setProductId(productEvent.getProductId())
            .setAvailable(productEvent.getVolume())
            .setCreatedOn(Instant.now().toEpochMilli())
            .build();
    }
}
