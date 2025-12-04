package com.codegym.ecommercemanage.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDTO {

    @NotEmpty
    private String name;

    @NotNull
    private Double price;

    private String description;

    private String image;

    @NotNull
    private Integer quantity;

    private Long categoryId;

}