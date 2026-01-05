package com.codegym.ecommercemanage.controller.admin;

import com.codegym.ecommercemanage.dto.request.StockRequest;
import com.codegym.ecommercemanage.dto.response.StockReportResponse;
import com.codegym.ecommercemanage.model.StockHistory;
import com.codegym.ecommercemanage.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    // ================= IMPORT =================
    @PostMapping("/import")
    public ResponseEntity<String> importStock(
            @Valid @RequestBody StockRequest request
    ) {
        stockService.importStock(request);
        return ResponseEntity.ok("Nhập kho thành công");
    }

    // ================= EXPORT =================
    @PostMapping("/export")
    public ResponseEntity<String> exportStock(
            @Valid @RequestBody StockRequest request
    ) {
        stockService.exportStock(request);
        return ResponseEntity.ok("Xuất kho thành công");
    }

    // ================= HISTORY ALL =================
    @GetMapping("/history")
    public ResponseEntity<List<StockHistory>> history() {
        return ResponseEntity.ok(stockService.getHistory());
    }

    // ================= HISTORY BY PRODUCT =================
    @GetMapping("/history/{productId}")
    public ResponseEntity<List<StockHistory>> historyByProduct(
            @PathVariable Integer productId
    ) {
        return ResponseEntity.ok(
                stockService.getHistoryByProduct(productId)
        );
    }
    // ================= REPORT =================
    @GetMapping("/report/{productId}")
    public ResponseEntity<StockReportResponse> report(
            @PathVariable Integer productId
    ) {
        return ResponseEntity.ok(
                stockService.getStockReport(productId)
        );
    }

}
