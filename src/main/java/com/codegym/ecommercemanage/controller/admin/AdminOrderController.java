package com.codegym.ecommercemanage.controller.admin;

import com.codegym.ecommercemanage.model.Order;
import com.codegym.ecommercemanage.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    // ================= 1. XEM TẤT CẢ ĐƠN HÀNG =================
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // ================= 2. XEM CHI TIẾT ĐƠN HÀNG =================
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // ================= 3. CẬP NHẬT TRẠNG THÁI =================
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công");
    }
}
