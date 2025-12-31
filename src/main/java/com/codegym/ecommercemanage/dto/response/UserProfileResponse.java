package com.codegym.ecommercemanage.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private String address;
    private String email;
    private String status;
    private Set<String> roles;
}