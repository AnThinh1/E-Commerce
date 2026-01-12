package com.codegym.ecommercemanage.dto.response;

import com.codegym.ecommercemanage.model.Category;
import com.codegym.ecommercemanage.model.Role;
import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private String address;
    private String email;
    private Set<String> roles;
    private Set<CategorySimpleResponse> managedCategories;
}