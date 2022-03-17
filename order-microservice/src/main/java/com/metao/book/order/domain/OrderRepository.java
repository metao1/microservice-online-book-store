package com.metao.book.order.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, OrderId> {

        Optional<OrderEntity> getOrderByProductId(ProductId productId);
}
