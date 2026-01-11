package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.request.CommentRequestDTO;
import com.codegym.ecommercemanage.model.Comment;
import com.codegym.ecommercemanage.model.Product;
import com.codegym.ecommercemanage.model.User;
import com.codegym.ecommercemanage.repository.CommentRepository;
import com.codegym.ecommercemanage.repository.ProductRepository;
import com.codegym.ecommercemanage.repository.UserRepository; // Giả sử bạn đã có cái này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Comment addComment(CommentRequestDTO request) {
        // 1. Tìm Product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        // 2. Tìm User
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // 3. Tạo Entity Comment
        Comment comment = Comment.builder()
                .content(request.getContent())
                .product(product)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }
}
