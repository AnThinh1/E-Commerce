package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.request.LoginRequest;
import com.codegym.ecommercemanage.dto.request.RegisterRequest;
import com.codegym.ecommercemanage.dto.response.LoginResponse;
import com.codegym.ecommercemanage.dto.response.UserResponse;
import com.codegym.ecommercemanage.exceptions.DuplicateUserNameException;
import com.codegym.ecommercemanage.model.Role;
import com.codegym.ecommercemanage.model.User;
import com.codegym.ecommercemanage.repository.RoleRepository;
import com.codegym.ecommercemanage.repository.UserRepository;
import com.codegym.ecommercemanage.service.jwt.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LoginService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    // SỬA: Nhận RegisterRequest -> Trả UserResponse
    public UserResponse register(RegisterRequest request) throws DuplicateUserNameException {
        if (repo.existsByUsername(request.getUsername())) {
            throw new DuplicateUserNameException("Tên đăng nhập '" + request.getUsername() + "' đã tồn tại!");
        }

        // (Khuyên dùng) Nên check cả email để tránh 1 email đăng ký nhiều acc
        if (repo.existsByEmail(request.getEmail())) {
            throw new DuplicateUserNameException("Email này đã được sử dụng!");
        }

        // 1. Chuyển đổi từ DTO (RegisterRequest) sang Entity (User)
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());

        // Gán quyền mặc định
        Role userRole = roleRepo.findByName("ROLE_USER");
        user.setRoles(Set.of(userRole));

        // 2. Lưu xuống database
        User savedUser = repo.save(user);

        // 3. Chuyển đổi từ Entity sang DTO (UserResponse) để trả về
        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .fullName(savedUser.getFullName())
                .phone(savedUser.getPhone())
                .address(savedUser.getAddress())
                .build();
    }

    // SỬA: Nhận LoginRequest -> Trả LoginResponse
    public LoginResponse verify(LoginRequest request) {
        // 1. Xác thực username/password
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. Nếu xác thực thành công
        if (authentication.isAuthenticated()) {
            // Tạo token
            String token = jwtService.generateToken(request.getUsername());

            // Lấy thông tin user từ database để trả về frontend (để hiện tên, avatar, phân quyền...)
            User user = repo.findByUsername(request.getUsername()); // Giả sử repo có hàm này

            // Lấy danh sách quyền (Roles) từ đối tượng User hoặc Authentication
            List<String> roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            // Trả về LoginResponse đầy đủ
            return new LoginResponse(token, "Bearer", user.getId(), user.getUsername(), user.getFullName(), roles);
        }

        // Nên ném lỗi ra để Controller bắt (Global Exception Handler), nhưng tạm thời return null hoặc throw
        throw new RuntimeException("Login failed");
    }
}