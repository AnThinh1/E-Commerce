package com.codegym.ecommercemanage.dto.response;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class ProductDetailResponseDTO {
    // Thông tin Product
    private Integer id;
    private String name;
    private Double price;
    private String description;
    private String image;
    private String status;
    private Integer quantity;
    private String categoryName; // Lấy tên category thay vì object

    // Danh sách comment
    private List<CommentResponseDTO> comments;
}
