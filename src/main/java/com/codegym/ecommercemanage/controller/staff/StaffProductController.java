package com.codegym.ecommercemanage.controller.staff;

import com.codegym.ecommercemanage.dto.request.ProductFilterRequest;
import com.codegym.ecommercemanage.dto.request.ProductRequest;
import com.codegym.ecommercemanage.model.UserPrincipal;
import com.codegym.ecommercemanage.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/products")
@RequiredArgsConstructor
public class StaffProductController {

    private final ProductService productService;

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        productService.updateProductByStaff(
                id,
                request,
                principal.getId()
        );
        return ResponseEntity.ok("Cập nhật sản phẩm thành công");
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
