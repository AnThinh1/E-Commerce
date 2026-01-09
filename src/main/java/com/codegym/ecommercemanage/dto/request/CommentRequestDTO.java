package com.codegym.ecommercemanage.dto.request;

import lombok.Data;

@Data
public class CommentRequestDTO {
    private Long userId;    // ID người dùng bình luận
    private Integer productId; // ID sản phẩm
    private String content; // Nội dung
}
