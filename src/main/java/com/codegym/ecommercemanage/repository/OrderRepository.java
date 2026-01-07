package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
