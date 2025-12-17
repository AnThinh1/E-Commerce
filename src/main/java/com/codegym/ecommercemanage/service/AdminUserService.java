package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.request.RegisterRequest;
import com.codegym.ecommercemanage.dto.response.UserResponse;
import com.codegym.ecommercemanage.model.Role;
import com.codegym.ecommercemanage.model.User;
import com.codegym.ecommercemanage.repository.RoleRepository;
import com.codegym.ecommercemanage.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminUserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder encoder;

    public AdminUserService(UserRepository userRepo,
                            RoleRepository roleRepo,
                            BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    // ==========================
    // 1️⃣ LIST USER
    // ==========================
    public List<UserResponse> getAll() {
        return userRepo.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .phone(user.getPhone())
                        .address(user.getAddress())
                        .build()
                )
                .toList();
    }

    // ==========================
    // 2️⃣ GET DETAIL
    // ==========================
    public UserResponse getById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .build();
    }

    // ==========================
    // 3️⃣ CREATE USER
    // ==========================
    public UserResponse createUser(RegisterRequest request, String roleName) {

        if (userRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        Role role = roleRepo.findByName(roleName);
        if (role == null) {
            throw new RuntimeException("Role not found: " + roleName);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());

        // ✅ KHÔNG DÙNG Set.of
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User saved = userRepo.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .fullName(saved.getFullName())
                .phone(saved.getPhone())
                .address(saved.getAddress())
                .build();
    }

    // ==========================
    // 4️⃣ UPDATE USER (PARTIAL)
    // ==========================
    public UserResponse updateUser(Long id, RegisterRequest request, String roleName) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // update từng field (PATCH style)
        if (request.getFullName() != null)
            user.setFullName(request.getFullName());

        if (request.getPhone() != null)
            user.setPhone(request.getPhone());

        if (request.getAddress() != null)
            user.setAddress(request.getAddress());

        if (request.getEmail() != null)
            user.setEmail(request.getEmail());

        if (roleName != null) {
            Role role = roleRepo.findByName(roleName);
            if (role == null) {
                throw new RuntimeException("Role not found: " + roleName);
            }

            // 🔥 FIX QUAN TRỌNG
            user.getRoles().clear();
            user.getRoles().add(role);
        }

        User saved = userRepo.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .fullName(saved.getFullName())
                .phone(saved.getPhone())
                .address(saved.getAddress())
                .build();
    }

    // ==========================
    // 5️⃣ DELETE USER
    // ==========================
    public void delete(Long id) {
        if (!userRepo.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepo.deleteById(id);
    }
}
