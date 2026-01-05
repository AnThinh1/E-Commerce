package com.codegym.ecommercemanage.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockRequest {

    @NotNull
    private Integer productId;

    @NotNull
    @Min(1)
    private Integer quantity;

    private String note;
}
