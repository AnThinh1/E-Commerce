package com.codegym.ecommercemanage.controller.admin;


import com.codegym.ecommercemanage.dto.request.ProductFilterRequest;
import com.codegym.ecommercemanage.model.Product;
import com.codegym.ecommercemanage.model.UserPrincipal;
import com.codegym.ecommercemanage.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable int id,
            @RequestPart("product") Product productDetails,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            // 1. Tìm sản phẩm cũ
            Product existingProduct = productService.findById(id);
            if (existingProduct == null) {
                return ResponseEntity.notFound().build();
            }

            // 2. Cập nhật thông tin (Giữ nguyên ID, chỉ sửa nội dung)
            existingProduct.setName(productDetails.getName());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setQuantity(productDetails.getQuantity());
            existingProduct.setDescription(productDetails.getDescription());
            existingProduct.setStatus(productDetails.getStatus());
            existingProduct.setCategory(productDetails.getCategory());

            // 3. Xử lý ảnh: Chỉ thay đổi nếu người dùng có chọn ảnh mới
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();

                // Lưu file vào thư mục uploads
                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

                // Gán tên ảnh mới vào DB
                existingProduct.setImage(fileName);
            }
            // Nếu không chọn ảnh mới -> Giữ nguyên ảnh cũ (không làm gì cả)

            // 4. Lưu lại
            Product updatedProduct = productService.save(existingProduct);
            return ResponseEntity.ok(updatedProduct);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi Update: " + e.getMessage());
        }
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
    @GetMapping("/filter")
    public ResponseEntity<?> filterProducts(
            ProductFilterRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                productService.filterProducts(
                        request,
                        principal.getId()
                )
        );
    }
}
