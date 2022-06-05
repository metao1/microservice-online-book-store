package com.metao.book.order.domain;

import com.order.microservice.avro.OrderAvro;
import com.order.microservice.avro.Status;
import org.springframework.stereotype.Service;

@Service
public class OrderManageService {

    public OrderAvro confirm(OrderAvro orderPayment, OrderAvro orderStock) {
        var o = OrderAvro.newBuilder()
                .setOrderId(orderPayment.getOrderId())
                .setCustomerId(orderPayment.getCustomerId())
                .setProductId(orderPayment.getProductId())
                .setStatus(Status.ACCEPT)
                .setQuantity(orderPayment.getQuantity())
                .setPrice(orderPayment.getPrice())
                .setSource(orderPayment.getSource())
                .setCurrency(orderPayment.getCurrency())
                .build();
        if (orderPayment.getStatus().equals(Status.ACCEPT) &&
                orderStock.getStatus().equals(Status.ACCEPT)) {
            o.setStatus(Status.CONFIRM);
        } else if (orderPayment.getStatus().equals(Status.REJECT) &&
                orderStock.getStatus().equals(Status.REJECT)) {
            o.setStatus(Status.REJECT);
        } else if (orderPayment.getStatus().equals(Status.REJECT) ||
                orderStock.getStatus().equals(Status.REJECT)) {
            String source = orderPayment.getStatus().equals(Status.REJECT) ? "PAYMENT" : "STOCK";
            o.setStatus(Status.ROLLBACK);
            o.setSource(source);
        }
        return o;
    }

}