package com.codegym.ecommercemanage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class CategorySimpleResponse {

    private Long id;
    private String categoryName;
}

