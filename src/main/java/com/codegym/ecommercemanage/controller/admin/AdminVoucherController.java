package com.codegym.ecommercemanage.controller.admin;

import com.codegym.ecommercemanage.dto.request.VoucherRequest;
import com.codegym.ecommercemanage.dto.response.VoucherResponse;
import com.codegym.ecommercemanage.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vouchers")
public class AdminVoucherController {

    @Autowired
    private VoucherService voucherService;

    // CREATE
    @PostMapping
    public ResponseEntity<VoucherResponse> create(@RequestBody VoucherRequest request) {
        return ResponseEntity.ok(voucherService.createVoucher(request));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<VoucherResponse> update(
            @PathVariable Long id,
            @RequestBody VoucherRequest request
    ) {
        return ResponseEntity.ok(voucherService.updateVoucher(id, request));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<VoucherResponse>> getAll() {
        return ResponseEntity.ok(voucherService.getAllVouchers());
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<VoucherResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(voucherService.getVoucherById(id));
    }

    // ENABLE / DISABLE
    @PatchMapping("/{id}/active")
    public ResponseEntity<?> toggle(
            @PathVariable Long id,
            @RequestParam boolean active
    ) {
        voucherService.toggleVoucher(id, active);
        return ResponseEntity.ok("Cập nhật trạng thái thành công");
    }
}
