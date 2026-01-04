package com.codegym.ecommercemanage.controller;

import com.codegym.ecommercemanage.dto.request.LoginRequest;
import com.codegym.ecommercemanage.dto.request.RegisterRequest;
import com.codegym.ecommercemanage.dto.response.LoginResponse;
import com.codegym.ecommercemanage.dto.response.UserResponse;
import com.codegym.ecommercemanage.exceptions.DuplicateUserNameException;
import com.codegym.ecommercemanage.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    @Autowired
    private LoginService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Cố gắng đăng ký
            UserResponse response = service.register(request);

            // Nếu thành công -> Trả về 200 OK + Data User
            return ResponseEntity.ok(response);

        } catch (DuplicateUserNameException e) {
            // Nếu bắt được lỗi trùng tên -> Trả về 400 Bad Request + Message lỗi
            // e.getMessage() sẽ lấy chuỗi "Tên đăng nhập... đã tồn tại" bạn ném ra ở Service
            return ResponseEntity.badRequest().body(e.getMessage());

            // Hoặc muốn chuyên nghiệp hơn thì dùng status 409 (Conflict):
            // return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("Đang nhận request login: " + request.getUsername());

            // Gọi service xác thực
            LoginResponse response = service.verify(request);

            System.out.println("Login thành công!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // In lỗi ra màn hình Console của IntelliJ để xem
            e.printStackTrace();
            return ResponseEntity.status(401).body("Lỗi đăng nhập: " + e.getMessage());
        }
    }
}