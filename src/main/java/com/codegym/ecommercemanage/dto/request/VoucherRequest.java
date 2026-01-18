package com.codegym.ecommercemanage.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class VoucherRequest {

    private String code;
    private Integer discountPercent;
    private Long maxDiscount;
    private Long minOrderValue;
    private Integer quantity;
    private Boolean active;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Set<Integer> categoryIds;
}
