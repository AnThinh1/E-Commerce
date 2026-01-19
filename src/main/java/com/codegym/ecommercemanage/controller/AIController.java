package com.codegym.ecommercemanage.controller;

import com.codegym.ecommercemanage.dto.response.PredictionResponseDTO;
import com.codegym.ecommercemanage.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin("*") // Cho phép React gọi
public class AIController {

    @Autowired
    private AIService aiService;

    // API dành cho Admin bấm nút "Train AI"
    @PostMapping("/train")
    public ResponseEntity<String> trainAI() {
        String result = aiService.trainModel();
        return ResponseEntity.ok(result);
    }

    // API dự đoán (Khi admin nhập form tạo/sửa sản phẩm)
    // Ví dụ gọi: POST /api/ai/predict?categoryId=1&price=500000
    @PostMapping("/predict")
    public ResponseEntity<?> predict(@RequestParam Integer categoryId,
                                     @RequestParam Long price) {

        PredictionResponseDTO result = aiService.predictPotential(categoryId, price);

        if (result == null) {
            return ResponseEntity.status(500).body("Lỗi kết nối tới AI Service hoặc Model chưa được train");
        }
        return ResponseEntity.ok(result);
    }
}