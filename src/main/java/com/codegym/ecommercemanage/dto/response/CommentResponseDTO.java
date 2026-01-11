package com.codegym.ecommercemanage.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String userFullName; // Chỉ lấy tên, không lấy pass/email
    private LocalDateTime createdAt;
}
