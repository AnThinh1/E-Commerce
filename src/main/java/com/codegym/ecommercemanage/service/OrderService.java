package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.response.RevenueByDayResponse;
import com.codegym.ecommercemanage.dto.response.RevenueByMonthResponse;
import com.codegym.ecommercemanage.model.*;
import com.codegym.ecommercemanage.repository.OrderRepository;
import com.codegym.ecommercemanage.repository.ProductRepository;
import com.codegym.ecommercemanage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class) // Quan trọng: Lỗi bất kỳ đâu sẽ hoàn tác tất cả (Rollback)
    public Order placeOrder(User user, List<CartItem> cartItems) {

        // 1. Khởi tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        long totalPrice = 0;
        List<OrderItem> orderItemList = new ArrayList<>();

        // 2. Duyệt qua từng sản phẩm trong giỏ
        for (CartItem itemDTO : cartItems) {
            // Lấy sản phẩm từ DB
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại ID: " + itemDTO.getProductId()));

            // CHECK TỒN KHO (Logic mới)
            if (product.getQuantity() < itemDTO.getQuantity()) {
                throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ hàng. Chỉ còn: " + product.getQuantity());
            }

            // TRỪ TỒN KHO (Logic mới)
            int newStock = product.getQuantity() - itemDTO.getQuantity();
            product.setQuantity(newStock);

            // Nếu hết hàng thì có thể set trạng thái luôn (Tùy chọn)
            if (newStock > 0) {
                product.setStatus("ACTIVE");
            } else {
                product.setStatus("OUT_OF_STOCK");
            }

            // Lưu lại thông tin sản phẩm mới vào DB
            productRepository.save(product);

            // D. Tạo OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(product.getPrice()); // Lấy giá từ DB

            orderItemList.add(orderItem);
            totalPrice += product.getPrice().longValue() * itemDTO.getQuantity();
        }

        // 3. Hoàn tất Order
        order.setItems(orderItemList);
        order.setTotalPrice(totalPrice);

        return orderRepository.save(order);

    }

    public List<RevenueByDayResponse> getRevenueByDay() {
        return orderRepository.revenueByDay().stream()
                .map(obj -> new RevenueByDayResponse(
                        obj[0].toString(),
                        ((Number) obj[1]).longValue()
                ))
                .toList();
    }

    public List<RevenueByMonthResponse> getRevenueByMonth() {
        return orderRepository.revenueByMonth().stream()
                .map(obj -> new RevenueByMonthResponse(
                        (Integer) obj[0],
                        (Integer) obj[1],
                        ((Number) obj[2]).longValue()
                ))
                .toList();
    }
    // ================= ADMIN =================
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        String currentStatus = order.getStatus();

        //  Không cho cập nhật nếu đã kết thúc
        if (currentStatus.equals("COMPLETED") || currentStatus.equals("CANCELLED")) {
            throw new RuntimeException("Đơn hàng đã kết thúc, không thể cập nhật");
        }

        //  HOÀN KHO KHI CANCEL (CHỈ KHI CÒN PENDING)
        if (newStatus.equals("CANCELLED") && currentStatus.equals("PENDING")) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        // Cập nhật trạng thái
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    // ================= USER =================

    // Xem tất cả đơn của user
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // Xem chi tiết 1 đơn của user
    public Order getOrderByUser(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Chặn xem đơn người khác
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền xem đơn hàng này");
        }

        return order;
    }
    @Transactional
    public void cancelOrderByUser(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Chỉ cho huỷ đơn của chính mình
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền huỷ đơn này");
        }

        // Chỉ cho huỷ khi còn PENDING
        if (!order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Chỉ có thể huỷ đơn khi đang chờ xử lý");
        }

        // Hoàn kho
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        // Cập nhật trạng thái
        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    public List<RevenueByDayResponse> getRevenueByDayBetween(
            LocalDate from,
            LocalDate to
    ) {
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);

        return orderRepository.revenueByDayBetween(fromDate, toDate)
                .stream()
                .map(obj -> new RevenueByDayResponse(
                        obj[0].toString(),
                        ((Number) obj[1]).longValue()
                ))
                .toList();
    }

    public List<RevenueByMonthResponse> getRevenueByMonthBetween(
            LocalDate from,
            LocalDate to
    ) {
        LocalDateTime fromDate = from.atStartOfDay();
        LocalDateTime toDate = to.atTime(23, 59, 59);

        return orderRepository.revenueByMonthBetween(fromDate, toDate)
                .stream()
                .map(obj -> new RevenueByMonthResponse(
                        (Integer) obj[0],
                        (Integer) obj[1],
                        ((Number) obj[2]).longValue()
                ))
                .toList();
    }

    public List<Order> getOrdersByStaff(Long staffId) {
        // 1. Lấy thông tin Staff
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên ID: " + staffId));

        // 2. Lấy danh sách Category mà Staff này quản lý
        Set<Category> managedCategories = staff.getManagedCategories();

        // 3. Nếu Staff không quản lý category nào, trả về rỗng
        if (managedCategories == null || managedCategories.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. Truy vấn các đơn hàng có sản phẩm thuộc các category này
        return orderRepository.findOrdersByCategories(managedCategories);
    }

    public Order getOrderByIdForStaff(Long orderId, Long staffId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        Set<Category> managedCategories = staff.getManagedCategories();
// Kiểm tra xem trong đơn hàng có sản phẩm nào thuộc category staff quản lý không

        boolean isManaged = order.getItems().stream()

                .anyMatch(item -> managedCategories.contains(item.getProduct().getCategory()));
        if (!isManaged) {

            throw new RuntimeException("Bạn không có quyền xem đơn hàng này (Khác Category quản lý)");

        }
        return order;
    }
}
