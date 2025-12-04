package com.codegym.ecommercemanage.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CategoryDTO {
    @NotEmpty
    private String categoryName;
}