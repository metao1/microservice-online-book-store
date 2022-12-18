package com.metao.book.order.domain;


import java.util.List;
import java.util.Optional;

import com.metao.book.order.application.dto.OrderDTO;
import com.metao.book.shared.OrderEvent;

public interface OrderServiceInterface {

    void saveOrder(OrderEvent orderEntity);

    Optional<OrderDTO> getOrderByOrderId(String orderId);

    Optional<List<OrderEvent>> getAllOrdersPageable(int from, int to);

}
