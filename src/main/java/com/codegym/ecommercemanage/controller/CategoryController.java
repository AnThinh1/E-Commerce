package com.codegym.ecommercemanage.controller;

import com.codegym.ecommercemanage.model.Category;
import com.codegym.ecommercemanage.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Lấy tất cả (Public)
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    // Thêm mới (Admin) -> URL: /api/admin/categories
    @PostMapping("/admin/categories")
    public ResponseEntity<?> addCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.save(category));
    }

    // Cập nhật (Admin) -> URL: /api/admin/categories/{id}
    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        Category existingCategory = categoryService.findById(id);
        if (existingCategory == null) {
            return ResponseEntity.notFound().build();
        }
        category.setId(id);
        return ResponseEntity.ok(categoryService.save(category));
    }

    // Xóa (Admin) -> URL: /api/admin/categories/{id}
    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        categoryService.delete(id);
        return ResponseEntity.ok("Xóa thành công");
    }
}