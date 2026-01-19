package com.codegym.ecommercemanage.dto.response;
import lombok.Data;

@Data
public class PredictionResponseDTO {
    private Integer categoryId;
    private Long price;

    // Đổi tên biến cho đúng nghĩa
    private Double predicted_revenue;

    private String potential;
}
