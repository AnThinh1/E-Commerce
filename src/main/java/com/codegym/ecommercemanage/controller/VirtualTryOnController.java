package com.codegym.ecommercemanage.controller;

import com.codegym.ecommercemanage.service.VirtualTryOnService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/virtual-try-on")
public class VirtualTryOnController {

    private final VirtualTryOnService virtualTryOnService;

    public VirtualTryOnController(VirtualTryOnService virtualTryOnService) {
        this.virtualTryOnService = virtualTryOnService;
    }

    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> generateTryOn(
            @RequestParam("productId") String productId,
            @RequestParam("userImage") MultipartFile userImage) {

        try {
            // Gọi service xử lý
            String base64Image = virtualTryOnService.processTryOn(productId, userImage);

            // Tạo JSON response: { "image": "chuoi_base_64..." }
            Map<String, String> response = new HashMap<>();
            response.put("image", base64Image);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi xử lý: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
