package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.model.Category;
import com.codegym.ecommercemanage.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    // 👇 SỬA LỖI Ở ĐÂY
    public Category findById(Integer id) {
        // findById trả về Optional, cần gọi .orElse(null) để lấy object ra
        return categoryRepository.findById(id).orElse(null);
    }

    // Thêm các hàm này để dùng trong Controller
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void delete(Integer id) {
        categoryRepository.deleteById(id);
    }
}