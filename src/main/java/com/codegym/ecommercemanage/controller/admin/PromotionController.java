package com.codegym.ecommercemanage.controller.admin;

import com.codegym.ecommercemanage.dto.request.PromotionRequest;
import com.codegym.ecommercemanage.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/promotions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PromotionRequest request) {
        return ResponseEntity.ok(promotionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequest request) {
        return ResponseEntity.ok(promotionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        promotionService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}