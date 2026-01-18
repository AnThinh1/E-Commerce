package com.codegym.ecommercemanage.controller;

import com.codegym.ecommercemanage.dto.request.PlaceOrderRequest;
import com.codegym.ecommercemanage.model.CartItem;
import com.codegym.ecommercemanage.model.Order;
import com.codegym.ecommercemanage.model.User;
import com.codegym.ecommercemanage.model.UserPrincipal; // Import class trong ảnh của bạn
import com.codegym.ecommercemanage.repository.UserRepository;
import com.codegym.ecommercemanage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/user/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestBody PlaceOrderRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
        }

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order savedOrder = orderService.placeOrder(
                user,
                request.getCartItems(),
                request.getVoucherCode()
        );

        return ResponseEntity.ok(savedOrder);
    }
    // ================= XEM DANH SÁCH ĐƠN =================
    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal.getId();
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    // ================= XEM CHI TIẾT ĐƠN =================
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getMyOrderDetail(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal.getId();
        return ResponseEntity.ok(orderService.getOrderByUser(orderId, userId));
    }
    // ================= SỬA TRẠNG THÁI =================
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelMyOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }

        orderService.cancelOrderByUser(orderId, userPrincipal.getId());
        return ResponseEntity.ok("Huỷ đơn hàng thành công");
    }
}

