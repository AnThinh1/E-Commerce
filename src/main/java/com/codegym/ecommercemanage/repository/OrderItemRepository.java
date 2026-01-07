package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
