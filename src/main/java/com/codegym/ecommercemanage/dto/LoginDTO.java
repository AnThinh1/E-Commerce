package com.codegym.ecommercemanage.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginDTO {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}