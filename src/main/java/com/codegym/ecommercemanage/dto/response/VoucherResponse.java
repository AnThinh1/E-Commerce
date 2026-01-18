package com.codegym.ecommercemanage.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class VoucherResponse {

    private Long id;
    private String code;
    private Integer discountPercent;
    private Long maxDiscount;
    private Long minOrderValue;
    private Integer quantity;
    private Boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Set<String> categories; // tên category
}
