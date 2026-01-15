package com.codegym.ecommercemanage.controller.staff; // Lưu ý package

import com.codegym.ecommercemanage.model.Order;
import com.codegym.ecommercemanage.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/orders")
@RequiredArgsConstructor
public class StaffOrderController {

    private final OrderService orderService;

    // Giả sử: ID của Staff đang đăng nhập được truyền vào qua @RequestParam hoặc lấy từ Token.
    // Ở đây mình ví dụ dùng @RequestParam cho đơn giản giống flow hiện tại của bạn.
    // Trong thực tế, bạn nên lấy userId từ SecurityContextHolder (Principal).

    // XEM ĐƠN HÀNG THUỘC PHẠM VI QUẢN LÝ
    @GetMapping
    public ResponseEntity<List<Order>> getStaffOrders(@RequestParam Long staffId) {
        List<Order> orders = orderService.getOrdersByStaff(staffId);
        return ResponseEntity.ok(orders);
    }

    // XEM CHI TIẾT ĐƠN HÀNG (CÓ KIỂM TRA QUYỀN CATEGORY)
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getStaffOrderDetail(
            @PathVariable Long orderId,
            @RequestParam Long staffId
    ) {
        return ResponseEntity.ok(orderService.getOrderByIdForStaff(orderId, staffId));
    }

    // CẬP NHẬT TRẠNG THÁI (Staff cũng có thể cập nhật trạng thái đơn họ quản lý)
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Long staffId, // Thêm staffId để check quyền nếu cần thiết
            @RequestParam String status
    ) {
        // Tái sử dụng logic check quyền trong hàm getOrderByIdForStaff trước khi update
        // Hoặc bạn có thể viết 1 hàm updateOrderStatusForStaff riêng trong Service để chặt chẽ hơn
        orderService.getOrderByIdForStaff(orderId, staffId); // Dòng này để throw lỗi nếu không có quyền

        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công");
    }
}