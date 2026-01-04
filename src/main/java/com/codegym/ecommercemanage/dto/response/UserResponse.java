package com.codegym.ecommercemanage.dto.response;

import com.codegym.ecommercemanage.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder // Giúp tạo object nhanh gọn: UserResponse.builder()...build()
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private String address;
    // Tuyệt đối KHÔNG có password ở đây
    private Set<Role> roles;
}