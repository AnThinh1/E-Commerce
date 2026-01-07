package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.model.*;
import com.codegym.ecommercemanage.repository.OrderRepository;
import com.codegym.ecommercemanage.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional // Đảm bảo nếu có lỗi trong quá trình lưu item thì rollback toàn bộ Order
    public Order placeOrder(User user, List<CartItem> cartItems) {

        // 1. Khởi tạo Order mới
        Order order = new Order();
        order.setUser(user); // Gán người dùng hiện tại
        order.setStatus("PENDING"); // Trạng thái mặc định
        order.setCreatedAt(LocalDateTime.now());

        double totalPrice = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        // 2. Duyệt qua từng sản phẩm trong giỏ hàng
        for (CartItem itemDTO : cartItems) {
            // Tìm sản phẩm thực tế trong DB
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + itemDTO.getProductId()));

            // (Optional) Kiểm tra tồn kho ở đây nếu cần
            // if (product.getStock() < itemDTO.getQuantity()) { throw ... }

            // Tạo OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());

            // QUAN TRỌNG: Lấy giá từ DB (product.getPrice), không lấy từ frontend
            orderItem.setPrice(product.getPrice());

            orderItemList.add(orderItem);

            // Cộng dồn tổng tiền
            totalPrice += (product.getPrice() * itemDTO.getQuantity());
        }

        // 3. Gán danh sách item và tổng tiền vào Order
        order.setItems(orderItemList);
        order.setTotalPrice(totalPrice);

        // 4. Lưu vào Database
        // Vì CascadeType.ALL ở entity Order, nên nó sẽ tự lưu luôn các OrderItem
        return orderRepository.save(order);
    }
}