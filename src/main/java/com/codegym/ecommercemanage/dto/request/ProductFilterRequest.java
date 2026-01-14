package com.codegym.ecommercemanage.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterRequest {

    private Long minPrice;      // giá từ
    private Long maxPrice;      // giá đến
    private Long categoryId;    // lọc theo category
    private String sort;        // price_asc | price_desc
}
