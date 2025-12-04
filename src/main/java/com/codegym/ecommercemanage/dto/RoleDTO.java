package com.codegym.ecommercemanage.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RoleDTO {

    @NotEmpty
    private String roleName;
}