package com.codegym.ecommercemanage.dto;

import lombok.Data;

@Data
public class ProductUpdateDTO {

    private String name;

    private Double price;

    private String description;

    private String image;

    private Integer quantity;

    private Long categoryId;

}