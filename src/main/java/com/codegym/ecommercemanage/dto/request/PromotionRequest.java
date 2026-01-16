package com.codegym.ecommercemanage.dto.request;

import com.codegym.ecommercemanage.model.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class PromotionRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotNull
    private Double discountValue;

    @NotNull
    private DiscountType discountType;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean active;
}