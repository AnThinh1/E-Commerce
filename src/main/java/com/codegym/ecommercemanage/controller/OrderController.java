package com.codegym.ecommercemanage.controller;

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
            @RequestBody List<CartItem> cartItems,
            @AuthenticationPrincipal UserPrincipal userPrincipal // <--- KEY POINT: Lấy thẳng object này
    ) {
        // 1. Kiểm tra đăng nhập
        if (userPrincipal == null) {
            return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
        }

        // 2. Lấy ID trực tiếp từ UserPrincipal (không cần query DB để tìm username)
        Long userId = userPrincipal.getId();

        // 3. Lấy Entity User để gán vào Order
        // (Query theo ID nhanh hơn query theo Username)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 4. Gọi Service
        Order savedOrder = orderService.placeOrder(user, cartItems);

        return ResponseEntity.ok(savedOrder);
    }
}
