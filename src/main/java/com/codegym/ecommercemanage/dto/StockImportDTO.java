package com.codegym.ecommercemanage.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockImportDTO {

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    private String note;
}