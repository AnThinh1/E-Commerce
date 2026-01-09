package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;

@Service
public class VirtualTryOnService {

    private final HuggingFaceService huggingFaceService;
    private final ProductService productService;

    private static final String UPLOAD_DIR = "uploads/";

    public VirtualTryOnService(
            HuggingFaceService huggingFaceService,
            ProductService productService
    ) {
        this.huggingFaceService = huggingFaceService;
        this.productService = productService;
    }

    /**
     * Xử lý Virtual Try-On
     */
    public String processTryOn(String productId, MultipartFile userImage) throws IOException {

        System.out.println("=== VIRTUAL TRY-ON SERVICE ===");
        
        // 1. Lấy thông tin sản phẩm
        int id = Integer.parseInt(productId);
        Product product = productService.findById(id);

        if (product.getImage() == null) {
            throw new RuntimeException("Sản phẩm không có ảnh");
        }

        // 2. Đọc ảnh sản phẩm từ disk
        Path productPath = Paths.get(UPLOAD_DIR + product.getImage());
        if (!Files.exists(productPath)) {
            throw new RuntimeException("Không tìm thấy file ảnh: " + productPath);
        }

        byte[] personImageBytes = userImage.getBytes();          // Ảnh người dùng upload
        byte[] garmentImageBytes = Files.readAllBytes(productPath); // Ảnh quần áo từ product

        System.out.println("Product: " + product.getName());
        System.out.println("Person image: " + personImageBytes.length + " bytes");
        System.out.println("Garment image: " + garmentImageBytes.length + " bytes");

        // 3. Gọi Hugging Face API
        byte[] resultImage = huggingFaceService.generateVirtualTryOn(personImageBytes, garmentImageBytes);

        if (resultImage == null || resultImage.length == 0) {
            throw new RuntimeException("AI không trả về kết quả");
        }

        // 4. Convert sang Base64 để trả về frontend
        String base64Result = Base64.getEncoder().encodeToString(resultImage);
        
        System.out.println("✅ Virtual Try-On completed!");
        System.out.println("Result size: " + resultImage.length + " bytes");
        System.out.println("Base64 length: " + base64Result.length() + " chars");
        
        return base64Result;
    }
}
