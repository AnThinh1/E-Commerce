package com.codegym.ecommercemanage.repository;

import com.codegym.ecommercemanage.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // ================= DOANH THU THEO NGÀY =================
    @Query("""
        SELECT DATE(o.createdAt), SUM(o.totalPrice)
        FROM Order o
        WHERE o.status = 'PENDING'
        GROUP BY DATE(o.createdAt)
        ORDER BY DATE(o.createdAt)
    """)
    List<Object[]> revenueByDay();

    // ================= DOANH THU THEO THÁNG =================
    @Query("""
        SELECT YEAR(o.createdAt), MONTH(o.createdAt), SUM(o.totalPrice)
        FROM Order o
        WHERE o.status = 'PENDING'
        GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)
        ORDER BY YEAR(o.createdAt), MONTH(o.createdAt)
    """)
    List<Object[]> revenueByMonth();
}
