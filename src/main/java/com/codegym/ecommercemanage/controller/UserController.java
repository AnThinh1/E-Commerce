package com.codegym.ecommercemanage.controller;

import com.codegym.ecommercemanage.dto.request.LoginRequest;
import com.codegym.ecommercemanage.dto.request.RegisterRequest;
import com.codegym.ecommercemanage.dto.response.LoginResponse;
import com.codegym.ecommercemanage.dto.response.UserResponse;
import com.codegym.ecommercemanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(value = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    // SỬA: Nhận RegisterRequest, trả về ResponseEntity<UserResponse>
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        UserResponse response = service.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    // SỬA: Nhận LoginRequest, trả về ResponseEntity<LoginResponse>
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse response = service.verify(request);
        return ResponseEntity.ok(response);
    }
}