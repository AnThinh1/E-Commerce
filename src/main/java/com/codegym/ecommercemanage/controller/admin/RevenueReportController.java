package com.codegym.ecommercemanage.controller.admin;

import com.codegym.ecommercemanage.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class RevenueReportController {

    private final OrderService orderService;

    //DOANH THU THEO NGÀY
    @GetMapping("/revenue/day")
    public ResponseEntity<?> revenueByDay() {
        return ResponseEntity.ok(orderService.getRevenueByDay());
    }

    //DOANH THU THEO THÁNG
    @GetMapping("/revenue/month")
    public ResponseEntity<?> revenueByMonth() {
        return ResponseEntity.ok(orderService.getRevenueByMonth());
    }
}
