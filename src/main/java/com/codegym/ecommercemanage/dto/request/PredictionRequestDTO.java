package com.codegym.ecommercemanage.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionRequestDTO {
    private Integer categoryId;
    private Long price;
}
