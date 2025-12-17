package com.codegym.ecommercemanage.dto.request;

import jakarta.validation.constraints.NotBlank; // Dùng NotBlank tốt hơn NotEmpty (chặn cả dấu cách)
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Password không được để trống")
    private String password;
}