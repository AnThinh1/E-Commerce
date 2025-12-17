package com.codegym.ecommercemanage.dto.response;

import lombok.AllArgsConstructor; // Tạo constructor đầy đủ tham số
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;       // Chuỗi JWT
    private String type = "Bearer";
    private Long id;
    private String username;
    private String fullName;
    private List<String> roles; // Trả về quyền (VD: ["ROLE_ADMIN", "ROLE_USER"]) để React phân quyền menu
}