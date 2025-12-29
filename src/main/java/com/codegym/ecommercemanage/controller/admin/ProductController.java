package com.codegym.ecommercemanage.controller.admin;


import com.codegym.ecommercemanage.model.Product;
import com.codegym.ecommercemanage.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    @GetMapping
    public ResponseEntity<?> getProducts(){
        List<Product> productList = productService.findAll();
        return ResponseEntity.ok(productList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id){
        Product product = productService.findById(id);

        // Nâng cao: Kiểm tra nếu không tìm thấy thì trả về 404
        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody Product product){
        product.setId(id);
        Product updatedProduct = productService.update(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id){
        productService.delete(id);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content (Chuẩn REST)
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart("product") Product product,
            @RequestPart("imageFile") MultipartFile imageFile
    ) {
        try {
            // 1. Kiểm tra file có rỗng không
            if (imageFile.isEmpty()) {
                return ResponseEntity.badRequest().body("File ảnh không được để trống");
            }

            // 2. Tạo tên file duy nhất để tránh trùng lặp (VD: iphone.jpg -> unique_iphone.jpg)
            // Hoặc giữ nguyên tên gốc: String fileName = imageFile.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();

            // 3. Đường dẫn lưu file (Thư mục uploads ở gốc project)
            Path uploadPath = Paths.get("uploads");

            // Nếu thư mục chưa tồn tại thì tạo mới
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 4. Lưu file vật lý
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 5. Gán tên file vào đối tượng product để lưu xuống DB
            product.setImage(fileName);
            // Lưu ý: Category phải được xử lý ở đây nếu client chỉ gửi category_id trong JSON product

            Product createdProduct = productService.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi IO: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi Server: " + e.getMessage());
        }
    }
}
