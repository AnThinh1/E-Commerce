package com.codegym.ecommercemanage.controller;

import com.codegym.ecommercemanage.dto.request.CommentRequestDTO;
import com.codegym.ecommercemanage.dto.request.ProductFilterRequest;
import com.codegym.ecommercemanage.dto.response.ProductDetailResponseDTO;
import com.codegym.ecommercemanage.model.Product;
import com.codegym.ecommercemanage.model.UserPrincipal;
import com.codegym.ecommercemanage.service.CommentService;
import com.codegym.ecommercemanage.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductUserController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CommentService commentService; // Inject thêm CommentService

    // 1. Lấy danh sách sản phẩm (Giữ nguyên)
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    // 2. SỬA: Lấy chi tiết sản phẩm (Trả về ProductDetailResponseDTO kèm comment)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponseDTO> getProductById(@PathVariable int id) {
        ProductDetailResponseDTO productDetail = productService.getProductDetailWithComments(id);
        if (productDetail != null) {
            return ResponseEntity.ok(productDetail);
        }
        return ResponseEntity.notFound().build();
    }

    // 3. THÊM: API Đăng bình luận
    @PostMapping("/comments")
    public ResponseEntity<?> addComment(@RequestBody CommentRequestDTO commentRequest) {
        try {
            commentService.addComment(commentRequest);
            return ResponseEntity.ok("Bình luận thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/filter")
    public ResponseEntity<?> filterProducts(
            ProductFilterRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                productService.filterProducts(request, principal.getId())
        );
    }

}