package com.codegym.ecommercemanage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, message = "Password phải có ít nhất 6 ký tự") // Validate độ dài
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    private String phone;   // Có thể null
    private String address; // Có thể null
    private String email;
    // Nên thêm Email nếu dự án cần gửi mail xác nhận
    // @Email
    // private String email;
}