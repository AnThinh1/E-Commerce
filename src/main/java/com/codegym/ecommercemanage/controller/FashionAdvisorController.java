package com.codegym.ecommercemanage.controller;

import com.codegym.ecommercemanage.service.FashionAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@RestController
@RequestMapping("/api/fashion-advisor")
@RequiredArgsConstructor
public class FashionAdvisorController {

    private final FashionAdvisorService advisorService;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyzeOutfit(
            @RequestParam("image") MultipartFile image,
            @RequestParam("option") String option // 'evaluate', 'suggest_pants', 'suggest_shirt'
    ) {
        try {
            String advice = advisorService.getAdvice(image, option);
            // Trả về JSON
            return ResponseEntity.ok(Collections.singletonMap("result", advice));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi xử lý AI: " + e.getMessage());
        }
    }
}
