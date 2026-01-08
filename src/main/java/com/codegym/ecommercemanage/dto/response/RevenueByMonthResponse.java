package com.codegym.ecommercemanage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueByMonthResponse {
    private Integer year;
    private Integer month;
    private Long revenue;
}
