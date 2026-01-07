package com.codegym.ecommercemanage.controller;

import com.codegym.ecommercemanage.model.Product;
import com.codegym.ecommercemanage.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductUserController {

    @Autowired
    private ProductService productService;

    // Chỉ cần đúng 1 API lấy danh sách JSON
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    // API lấy chi tiết (nếu cần)
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        Product product = productService.findById(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    // KHÔNG CẦN HÀM serveFile NỮA VÌ WebConfig ĐÃ LO RỒI
}