package com.codegym.ecommercemanage.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CommentDTO {

    @NotEmpty
    private String comment;

    private Long productId;
}
