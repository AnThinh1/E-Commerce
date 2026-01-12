package com.codegym.ecommercemanage.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class StaffRequest {
    private String username;
    private String password;
    private String fullName;
    private String phone;
    private String address;
    private String email;
    private List<Integer> categoryIds;

}