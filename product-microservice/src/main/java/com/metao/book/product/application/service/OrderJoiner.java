package com.metao.book.product.application.service;

import com.metao.book.shared.OrderEvent;
import com.metao.book.shared.ReservationEvent;
import com.metao.book.shared.Status;
import javax.transaction.Transactional;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class OrderJoiner implements ValueJoiner<ReservationEvent, OrderEvent, OrderEvent> {

    /**
     * Return a joined value consisting of {@code value1} and {@code value2}.
     *
     * @param reservation the first value for joining
     * @param order       the second value for joining
     * @return the joined value
     */
    @Override
    public OrderEvent apply(ReservationEvent reservation, OrderEvent order) {
        switch (order.getStatus()) {
            case NEW -> {
                if (availableInStock(order, reservation)) {
                    acceptOrder(order, reservation);
                } else {
                    rejectOrder(order);
                }
            }
            case CONFIRM -> confirmOrder(order, reservation);
            case ROLLBACK -> rollbackOrder(order, reservation);
        }
        return order;
    }

    @Transactional
    void rollbackOrder(OrderEvent order, ReservationEvent reservation) {
        if (order.getSource().equalsIgnoreCase("STOCK")) {
            return;
        }
        reservation.setAvailable(reservation.getAvailable() - order.getQuantity());
        reservation.setReserved(reservation.getReserved() - order.getQuantity());
    }

    private void confirmOrder(OrderEvent order, ReservationEvent reservation) {
        reservation.setReserved(reservation.getReserved() - order.getQuantity());
    }

    void rejectOrder(OrderEvent order) {
        order.setStatus(Status.REJECT);
    }

    @Transactional
    void acceptOrder(OrderEvent order, ReservationEvent reservation) {
        reservation.setAvailable(reservation.getAvailable() - order.getQuantity());
        reservation.setReserved(reservation.getReserved() + order.getQuantity());
        order.setStatus(Status.ACCEPT);
    }

    private static boolean availableInStock(OrderEvent order, ReservationEvent reservation) {
        return order.getQuantity() <= reservation.getAvailable();
    }
}
