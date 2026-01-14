package com.codegym.ecommercemanage.controller.admin;

import com.codegym.ecommercemanage.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @GetMapping("/revenue/day/filter")
    public ResponseEntity<?> revenueByDayBetween(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return ResponseEntity.ok(
                orderService.getRevenueByDayBetween(from, to)
        );
    }
    @GetMapping("/revenue/month/filter")
    public ResponseEntity<?> revenueByMonthBetween(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    ) {
        return ResponseEntity.ok(
                orderService.getRevenueByMonthBetween(from, to)
        );
    }

}
