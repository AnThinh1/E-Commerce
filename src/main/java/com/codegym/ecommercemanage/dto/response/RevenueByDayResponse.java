package com.codegym.ecommercemanage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueByDayResponse {
    private String date;      // yyyy-MM-dd
    private Long revenue;
}
