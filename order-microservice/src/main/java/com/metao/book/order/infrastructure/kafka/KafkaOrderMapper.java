package com.metao.book.order.infrastructure.kafka;

import static com.metao.book.order.domain.OrderStatus.CONFIRMED;
import static com.metao.book.order.domain.OrderStatus.NEW;
import static com.metao.book.order.domain.OrderStatus.REJECTED;
import static com.metao.book.order.domain.OrderStatus.ROLLED_BACK;
import static com.metao.book.order.domain.OrderStatus.SUBMITTED;

import com.google.protobuf.Timestamp;
import com.metao.book.order.OrderCreatedEvent;
import com.metao.book.order.OrderCreatedEvent.Status;
import com.metao.book.order.domain.OrderEntity;
import com.metao.book.order.domain.dto.OrderDTO;
import com.metao.book.shared.domain.financial.Money;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

public class KafkaOrderMapper {

    public static OrderCreatedEvent toOrderCreatedEvent(OrderDTO dto) {
        return OrderCreatedEvent.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setCreateTime(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
            .setStatus(Status.NEW)
            .setCustomerId(dto.customerId())
            .setProductId(dto.productId())
            .setCurrency(dto.currency())
            .setQuantity(dto.quantity().doubleValue())
            .setPrice(dto.price().doubleValue())
            .build();
    }

    public static OrderCreatedEvent toOrderUpdatedEvent(OrderDTO dto) {
        OrderCreatedEvent.Status status = switch (dto.status()) {
            case "SUBMITTED" -> Status.SUBMITTED;
            case "REJECTED" -> Status.REJECTED;
            case "CONFIRMED" -> Status.CONFIRMED;
            case "ROLLED_BACK" -> Status.ROLLED_BACK;
            case "NEW" -> Status.NEW;
            default -> throw new IllegalStateException("Unexpected value: " + dto.status());
        };
        return OrderCreatedEvent.newBuilder()
            .setId(dto.orderId())
            .setCreateTime(Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build())
            .setStatus(status)
            .setCustomerId(dto.customerId())
            .setProductId(dto.productId())
            .setCurrency(dto.currency())
            .setQuantity(dto.quantity().doubleValue())
            .setPrice(dto.price().doubleValue())
            .build();
    }

    public static OrderEntity toEntity(OrderCreatedEvent oce) {
        var money = new Money(Currency.getInstance(oce.getCurrency()), BigDecimal.valueOf(oce.getPrice()));

        var status = switch (oce.getStatus()) {
            case SUBMITTED -> SUBMITTED;
            case REJECTED -> REJECTED;
            case NEW -> NEW;
            case CONFIRMED -> CONFIRMED;
            case ROLLED_BACK -> ROLLED_BACK;
            case UNRECOGNIZED -> null;
        };

        return new OrderEntity(
            oce.getCustomerId(),
            oce.getProductId(),
            BigDecimal.valueOf(oce.getQuantity()),
            money,
            status
        );
    }
}
