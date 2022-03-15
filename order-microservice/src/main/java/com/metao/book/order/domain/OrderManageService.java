package com.metao.book.order.domain;

import org.springframework.stereotype.Service;

@Service
public class OrderManageService {

    public Order confirm(Order orderPayment, Order orderStock) {
        Order order;
        if ((orderPayment.status().equals(orderStock.status()) && orderPayment.status() == Status.ACCEPT)) {
           order = Order.of(Status.CONFIRM, orderPayment);
        } else if (orderPayment.status().equals(orderPayment.status()) && orderPayment.status() == Status.REJECT ){
            order = Order.of(Status.REJECT, orderPayment);
        }else if (orderPayment.status().equals(Status.REJECT)){
            order = Order.of(Status.ROLLBACK, orderPayment);
        } else {
            order = Order.of(Status.ROLLBACK, orderPayment);
        }
        return order;
    }
}
