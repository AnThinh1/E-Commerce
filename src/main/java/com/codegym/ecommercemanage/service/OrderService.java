//package com.codegym.ecommercemanage.service;
//
//import com.codegym.ecommercemanage.dto.response.RevenueByDayResponse;
//import com.codegym.ecommercemanage.dto.response.RevenueByMonthResponse;
//import com.codegym.ecommercemanage.model.*;
//import com.codegym.ecommercemanage.repository.OrderRepository;
//import com.codegym.ecommercemanage.repository.ProductRepository;
//import com.codegym.ecommercemanage.repository.UserRepository;
//import com.codegym.ecommercemanage.repository.VoucherRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//
//@Service
//public class OrderService {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private VoucherRepository voucherRepository;
//
//    @Transactional(rollbackFor = Exception.class) // Quan trọng: Lỗi bất kỳ đâu sẽ hoàn tác tất cả (Rollback)
//    public Order placeOrder(User user, List<CartItem> cartItems) {
//
//        // 1. Khởi tạo Order
//        Order order = new Order();
//        order.setUser(user);
//        order.setStatus("PENDING");
//        order.setCreatedAt(LocalDateTime.now());
//
//        long totalPrice = 0;
//        List<OrderItem> orderItemList = new ArrayList<>();
//
//        // 2. Duyệt qua từng sản phẩm trong giỏ
//        for (CartItem itemDTO : cartItems) {
//            // Lấy sản phẩm từ DB
//            Product product = productRepository.findById(itemDTO.getProductId())
//                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại ID: " + itemDTO.getProductId()));
//
//            // CHECK TỒN KHO (Logic mới)
//            if (product.getQuantity() < itemDTO.getQuantity()) {
//                throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ hàng. Chỉ còn: " + product.getQuantity());
//            }
//
//            // TRỪ TỒN KHO (Logic mới)
//            int newStock = product.getQuantity() - itemDTO.getQuantity();
//            product.setQuantity(newStock);
//
//            // Nếu hết hàng thì có thể set trạng thái luôn (Tùy chọn)
//            if (newStock > 0) {
//                product.setStatus("ACTIVE");
//            } else {
//                product.setStatus("OUT_OF_STOCK");
//            }
//
//            // Lưu lại thông tin sản phẩm mới vào DB
//            productRepository.save(product);
//
//            // D. Tạo OrderItem
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setProduct(product);
//            orderItem.setQuantity(itemDTO.getQuantity());
//            orderItem.setPrice(product.getPrice()); // Lấy giá từ DB
//
//            orderItemList.add(orderItem);
//            totalPrice += product.getPrice().longValue() * itemDTO.getQuantity();
//        }
//
//        // 3. Hoàn tất Order
//        order.setItems(orderItemList);
//        order.setTotalPrice(totalPrice);
//
//        return orderRepository.save(order);
//
//    }
//
//    public List<RevenueByDayResponse> getRevenueByDay() {
//        return orderRepository.revenueByDay().stream()
//                .map(obj -> new RevenueByDayResponse(
//                        obj[0].toString(),
//                        ((Number) obj[1]).longValue()
//                ))
//                .toList();
//    }
//
//    public List<RevenueByMonthResponse> getRevenueByMonth() {
//        return orderRepository.revenueByMonth().stream()
//                .map(obj -> new RevenueByMonthResponse(
//                        (Integer) obj[0],
//                        (Integer) obj[1],
//                        ((Number) obj[2]).longValue()
//                ))
//                .toList();
//    }
//    // ================= ADMIN =================
//    public List<Order> getAllOrders() {
//        return orderRepository.findAll();
//    }
//
//    public Order getOrderById(Long orderId) {
//        return orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + orderId));
//    }
//
//    @Transactional
//    public void updateOrderStatus(Long orderId, String newStatus) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
//
//        String currentStatus = order.getStatus();
//
//        //  Không cho cập nhật nếu đã kết thúc
//        if (currentStatus.equals("COMPLETED") || currentStatus.equals("CANCELLED")) {
//            throw new RuntimeException("Đơn hàng đã kết thúc, không thể cập nhật");
//        }
//
//        //  HOÀN KHO KHI CANCEL (CHỈ KHI CÒN PENDING)
//        if (newStatus.equals("CANCELLED") && currentStatus.equals("PENDING")) {
//            for (OrderItem item : order.getItems()) {
//                Product product = item.getProduct();
//                product.setQuantity(product.getQuantity() + item.getQuantity());
//                productRepository.save(product);
//            }
//        }
//
//        // Cập nhật trạng thái
//        order.setStatus(newStatus);
//        orderRepository.save(order);
//    }
//
//    // ================= USER =================
//
//    // Xem tất cả đơn của user
//    public List<Order> getOrdersByUser(Long userId) {
//        return orderRepository.findByUserId(userId);
//    }
//
//    // Xem chi tiết 1 đơn của user
//    public Order getOrderByUser(Long orderId, Long userId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
//
//        // Chặn xem đơn người khác
//        if (!order.getUser().getId().equals(userId)) {
//            throw new RuntimeException("Không có quyền xem đơn hàng này");
//        }
//
//        return order;
//    }
//    @Transactional
//    public void cancelOrderByUser(Long orderId, Long userId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
//
//        // Chỉ cho huỷ đơn của chính mình
//        if (!order.getUser().getId().equals(userId)) {
//            throw new RuntimeException("Không có quyền huỷ đơn này");
//        }
//
//        // Chỉ cho huỷ khi còn PENDING
//        if (!order.getStatus().equals("PENDING")) {
//            throw new RuntimeException("Chỉ có thể huỷ đơn khi đang chờ xử lý");
//        }
//
//        // Hoàn kho
//        for (OrderItem item : order.getItems()) {
//            Product product = item.getProduct();
//            product.setQuantity(product.getQuantity() + item.getQuantity());
//            productRepository.save(product);
//        }
//
//        // Cập nhật trạng thái
//        order.setStatus("CANCELLED");
//        orderRepository.save(order);
//    }
//
//    public List<RevenueByDayResponse> getRevenueByDayBetween(
//            LocalDate from,
//            LocalDate to
//    ) {
//        LocalDateTime fromDate = from.atStartOfDay();
//        LocalDateTime toDate = to.atTime(23, 59, 59);
//
//        return orderRepository.revenueByDayBetween(fromDate, toDate)
//                .stream()
//                .map(obj -> new RevenueByDayResponse(
//                        obj[0].toString(),
//                        ((Number) obj[1]).longValue()
//                ))
//                .toList();
//    }
//
//    public List<RevenueByMonthResponse> getRevenueByMonthBetween(
//            LocalDate from,
//            LocalDate to
//    ) {
//        LocalDateTime fromDate = from.atStartOfDay();
//        LocalDateTime toDate = to.atTime(23, 59, 59);
//
//        return orderRepository.revenueByMonthBetween(fromDate, toDate)
//                .stream()
//                .map(obj -> new RevenueByMonthResponse(
//                        (Integer) obj[0],
//                        (Integer) obj[1],
//                        ((Number) obj[2]).longValue()
//                ))
//                .toList();
//    }
//
//    public List<Order> getOrdersByStaff(Long staffId) {
//        // 1. Lấy thông tin Staff
//        User staff = userRepository.findById(staffId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên ID: " + staffId));
//
//        // 2. Lấy danh sách Category mà Staff này quản lý
//        Set<Category> managedCategories = staff.getManagedCategories();
//
//        // 3. Nếu Staff không quản lý category nào, trả về rỗng
//        if (managedCategories == null || managedCategories.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        // 4. Truy vấn các đơn hàng có sản phẩm thuộc các category này
//        return orderRepository.findOrdersByCategories(managedCategories);
//    }
//
//    public Order getOrderByIdForStaff(Long orderId, Long staffId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
//        User staff = userRepository.findById(staffId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
//        Set<Category> managedCategories = staff.getManagedCategories();
//// Kiểm tra xem trong đơn hàng có sản phẩm nào thuộc category staff quản lý không
//
//        boolean isManaged = order.getItems().stream()
//
//                .anyMatch(item -> managedCategories.contains(item.getProduct().getCategory()));
//        if (!isManaged) {
//
//            throw new RuntimeException("Bạn không có quyền xem đơn hàng này (Khác Category quản lý)");
//
//        }
//        return order;
//    }
//
//
//    private void validateVoucher(Voucher voucher, long totalPrice) {
//
//        LocalDateTime now = LocalDateTime.now();
//
//        if (!voucher.getActive())
//            throw new RuntimeException("Voucher bị khoá");
//
//        if (voucher.getQuantity() <= 0)
//            throw new RuntimeException("Voucher đã hết lượt");
//
//        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate()))
//            throw new RuntimeException("Voucher hết hạn");
//
//        if (totalPrice < voucher.getMinOrderValue())
//            throw new RuntimeException("Đơn hàng chưa đủ điều kiện");
//    }
//
//
//    @Transactional(rollbackFor = Exception.class)
//    public Order placeOrder(User user, List<CartItem> cartItems, String voucherCode) {
//
//        Order order = new Order();
//        order.setUser(user);
//        order.setStatus("PENDING");
//        order.setCreatedAt(LocalDateTime.now());
//
//        long totalPrice = 0;
//        List<OrderItem> orderItems = new ArrayList<>();
//
//        // ===== 1. TẠO ORDER ITEM + TRỪ KHO =====
//        for (CartItem dto : cartItems) {
//            Product product = productRepository.findById(dto.getProductId())
//                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
//
//            if (product.getQuantity() < dto.getQuantity())
//                throw new RuntimeException("Không đủ hàng");
//
//            product.setQuantity(product.getQuantity() - dto.getQuantity());
//            productRepository.save(product);
//
//            OrderItem item = new OrderItem();
//            item.setOrder(order);
//            item.setProduct(product);
//            item.setQuantity(dto.getQuantity());
//            item.setPrice(product.getPrice());
//
//            orderItems.add(item);
//            totalPrice += product.getPrice() * dto.getQuantity();
//        }
//
//        order.setItems(orderItems);
//
//        // ===== 2. ÁP VOUCHER =====
//        long discountAmount = 0;
//
//        if (voucherCode != null && !voucherCode.isBlank()) {
//
//            Voucher voucher = voucherRepository.findByCode(voucherCode)
//                    .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));
//
//            validateVoucher(voucher, totalPrice);
//
//            // 2.1 Tính tổng tiền các sản phẩm hợp lệ
//            long discountableAmount = orderItems.stream()
//                    .filter(item -> voucher.getCategories()
//                            .contains(item.getProduct().getCategory()))
//                    .mapToLong(item -> item.getPrice() * item.getQuantity())
//                    .sum();
//
//            if (discountableAmount == 0)
//                throw new RuntimeException("Voucher không áp dụng cho đơn hàng này");
//
//            // 2.2 Tính % giảm
//            discountAmount = discountableAmount * voucher.getDiscountPercent() / 100;
//
//            // 2.3 Giới hạn max discount
//            if (voucher.getMaxDiscount() != null) {
//                discountAmount = Math.min(discountAmount, voucher.getMaxDiscount());
//            }
//
//            voucher.setQuantity(voucher.getQuantity() - 1);
//            voucherRepository.save(voucher);
//
//            order.setVoucher(voucher);
//            order.setDiscountAmount(discountAmount);
//        }
//
//        order.setTotalPrice(totalPrice - discountAmount);
//
//        return orderRepository.save(order);
//    }
//
//}

package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.response.RevenueByDayResponse;
import com.codegym.ecommercemanage.dto.response.RevenueByMonthResponse;
import com.codegym.ecommercemanage.model.*;
import com.codegym.ecommercemanage.repository.OrderRepository;
import com.codegym.ecommercemanage.repository.ProductRepository;
import com.codegym.ecommercemanage.repository.UserRepository;
import com.codegym.ecommercemanage.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    // ================== PLACE ORDER (CHUẨN DUY NHẤT) ==================
    @Transactional(rollbackFor = Exception.class)
    public Order placeOrder(User user, List<CartItem> cartItems, String voucherCode) {

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        long totalPrice = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        // ===== 1. TẠO ORDER ITEM + TRỪ KHO =====
        for (CartItem dto : cartItems) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            if (product.getQuantity() < dto.getQuantity()) {
                throw new RuntimeException(
                        "Sản phẩm '" + product.getName() + "' không đủ hàng. Chỉ còn: " + product.getQuantity()
                );
            }

            int newStock = product.getQuantity() - dto.getQuantity();
            product.setQuantity(newStock);
            product.setStatus(newStock > 0 ? "AVAILABLE" : "OUT_OF_STOCK");
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            item.setPrice(product.getPrice());

            orderItems.add(item);
            totalPrice += product.getPrice() * dto.getQuantity();
        }

        order.setItems(orderItems);

        // ===== 2. ÁP VOUCHER =====
        long discountAmount = 0;

        if (voucherCode != null && !voucherCode.isBlank()) {

            Voucher voucher = voucherRepository.findByCode(voucherCode)
                    .orElseThrow(() -> new RuntimeException("Voucher không tồn tại"));

            validateVoucher(voucher, totalPrice);

            // Chỉ giảm trên category hợp lệ
            long discountableAmount = orderItems.stream()
                    .filter(item ->
                            voucher.getCategories()
                                    .contains(item.getProduct().getCategory()))
                    .mapToLong(item -> item.getPrice() * item.getQuantity())
                    .sum();

            if (discountableAmount == 0) {
                throw new RuntimeException("Voucher không áp dụng cho sản phẩm nào trong đơn");
            }

            discountAmount = discountableAmount * voucher.getDiscountPercent() / 100;

            if (voucher.getMaxDiscount() != null) {
                discountAmount = Math.min(discountAmount, voucher.getMaxDiscount());
            }

            voucher.setQuantity(voucher.getQuantity() - 1);
            voucherRepository.save(voucher);

            order.setVoucher(voucher);
            order.setDiscountAmount(discountAmount);
        }

        order.setTotalPrice(totalPrice - discountAmount);

        return orderRepository.save(order);
    }

    // ================== VALIDATE VOUCHER ==================
    private void validateVoucher(Voucher voucher, long totalPrice) {

        LocalDateTime now = LocalDateTime.now();

        if (!Boolean.TRUE.equals(voucher.getActive())) {
            throw new RuntimeException("Voucher bị khoá");
        }

        if (voucher.getQuantity() <= 0) {
            throw new RuntimeException("Voucher đã hết lượt");
        }

        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
            throw new RuntimeException("Voucher hết hạn");
        }

        if (totalPrice < voucher.getMinOrderValue()) {
            throw new RuntimeException("Đơn hàng chưa đủ điều kiện áp voucher");
        }
    }

    // ================= USER =================
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderByUser(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền xem đơn hàng này");
        }

        return order;
    }

    @Transactional
    public void cancelOrderByUser(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền huỷ đơn này");
        }

        if (!order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Chỉ có thể huỷ đơn khi đang chờ xử lý");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            product.setStatus("ACTIVE");
            productRepository.save(product);
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    // ================= ADMIN =================
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getStatus().equals("COMPLETED") || order.getStatus().equals("CANCELLED")) {
            throw new RuntimeException("Đơn hàng đã kết thúc, không thể cập nhật");
        }

        if (newStatus.equals("CANCELLED") && order.getStatus().equals("PENDING")) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                product.setStatus("ACTIVE");
                productRepository.save(product);
            }
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    // ================= STAFF =================
    public List<Order> getOrdersByStaff(Long staffId) {

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        Set<Category> managedCategories = staff.getManagedCategories();
        if (managedCategories == null || managedCategories.isEmpty()) {
            return Collections.emptyList();
        }

        return orderRepository.findOrdersByCategories(managedCategories);
    }

    public Order getOrderByIdForStaff(Long orderId, Long staffId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        boolean allowed = order.getItems().stream()
                .anyMatch(item -> staff.getManagedCategories()
                        .contains(item.getProduct().getCategory()));

        if (!allowed) {
            throw new RuntimeException("Bạn không có quyền xem đơn hàng này");
        }

        return order;
    }

    // ================= REVENUE =================
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

    public List<RevenueByDayResponse> getRevenueByDayBetween(LocalDate from, LocalDate to) {
        return orderRepository.revenueByDayBetween(
                from.atStartOfDay(),
                to.atTime(23, 59, 59)
        ).stream().map(obj -> new RevenueByDayResponse(
                obj[0].toString(),
                ((Number) obj[1]).longValue()
        )).toList();
    }

    public List<RevenueByMonthResponse> getRevenueByMonthBetween(LocalDate from, LocalDate to) {
        return orderRepository.revenueByMonthBetween(
                from.atStartOfDay(),
                to.atTime(23, 59, 59)
        ).stream().map(obj -> new RevenueByMonthResponse(
                (Integer) obj[0],
                (Integer) obj[1],
                ((Number) obj[2]).longValue()
        )).toList();
    }
}
