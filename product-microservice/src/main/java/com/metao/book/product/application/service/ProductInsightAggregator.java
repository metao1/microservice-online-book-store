package com.metao.book.product.application.service;

import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ReservationEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Aggregator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductInsightAggregator implements Aggregator<String, OrderEvent, ReservationEvent> {

    /**
     * Compute a new aggregate from the key and value of a record and the current aggregate of the same key.
     *
     * @param key          the key of the record
     * @param order        the value of the record
     * @param productEvent the current aggregate value
     * @return the new aggregate value
     */
//    @Override
//    public ProductEvent apply(String key, OrderEvent order, ProductEvent productEvent) {
//        switch (order.getStatus()) {
//            case CONFIRM -> productEvent.setAmountReserved(productEvent.getAmountReserved() - order.getQuantity());
//            case ROLLBACK -> {
//                if (!order.getSource().equals(STOCK)) {
//                    rsv.setAmountAvailable(rsv.getAmountAvailable() + order.getQuantity());
//                    rsv.setAmountReserved(rsv.getAmountReserved() - order.getQuantity());
//                }
//            }
//            case NEW -> {
//                if (order.getQuantity() <= rsv.getAmountAvailable()) {
//                    productEvent.setAmountAvailable(rsv.getAmountAvailable() - order.getQuantity());
//                    productEvent.setAmountReserved(rsv.getAmountReserved() + order.getQuantity());
//                    order.setStatus(ACCEPT);
//                } else {
//                    order.setStatus(REJECT);
//                }
//                template.send(paymentKafkaTopic, order.getOrderId(), order)
//                    .addCallback(result -> log.info("Sent: {}",
//                        result != null ? result.getProducerRecord().value() : null), ex -> {
//                    });
//            }
//        }
//        log.info("{}", productEvent);
//        return rsv;
//    }

    /**
     * Compute a new aggregate from the key and value of a record and the current aggregate of the same key.
     *
     * @param key       the key of the record
     * @param value     the value of the record
     * @param aggregate the current aggregate value
     * @return the new aggregate value
     */
    @Override
    public ReservationEvent apply(String key, OrderEvent value, ReservationEvent aggregate) {
        return null;
    }
}
