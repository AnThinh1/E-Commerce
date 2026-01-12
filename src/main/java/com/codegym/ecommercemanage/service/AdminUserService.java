package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.request.RegisterRequest;
import com.codegym.ecommercemanage.dto.request.StaffRequest;
import com.codegym.ecommercemanage.dto.response.CategorySimpleResponse;
import com.codegym.ecommercemanage.dto.response.UserResponse;
import com.codegym.ecommercemanage.model.Role;
import com.codegym.ecommercemanage.model.User;
import com.codegym.ecommercemanage.repository.CategoryRepository;
import com.codegym.ecommercemanage.repository.RoleRepository;
import com.codegym.ecommercemanage.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.codegym.ecommercemanage.dto.request.StaffRequest;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AdminUserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final CategoryRepository categoryRepo;
    private final BCryptPasswordEncoder encoder;

    public AdminUserService(UserRepository userRepo,
                            RoleRepository roleRepo,
                            CategoryRepository categoryRepo,
                            BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.categoryRepo = categoryRepo;
        this.encoder = encoder;
    }

    // ==========================
    // COMMON MAPPER
    // ==========================
    private UserResponse toResponse(User user) {

        // ✅ COPY roles
        Set<String> roleNames = user.getRoles() == null
                ? Set.of()
                : user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        // ✅ COPY categories (RẤT QUAN TRỌNG)
        Set<CategorySimpleResponse> categories =
                user.getManagedCategories() == null
                        ? Set.of()
                        : new HashSet<>(user.getManagedCategories())
                        .stream()
                        .map(c -> new CategorySimpleResponse(
                                c.getId().longValue(),
                                c.getCategoryName()
                        ))
                        .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .email(user.getEmail())
                .roles(roleNames)
                .managedCategories(categories)
                .build();
    }

    // ==========================
    // 1️⃣ LIST USER
    // ==========================
    public List<UserResponse> getAll() {
        return userRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================
    // 2️⃣ GET DETAIL
    // ==========================
    public UserResponse getById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toResponse(user);
    }

    // ==========================
    // 3️⃣ CREATE USER (ADMIN / USER)
    // ==========================
    public UserResponse createUser(RegisterRequest request, String roleName) {

        if (userRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

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

        user.getRoles().add(role);

        return toResponse(userRepo.save(user));
    }

    // ==========================
    // 4️⃣ UPDATE USER
    // ==========================
    public UserResponse updateUser(Long id, RegisterRequest request, String roleName) {

        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getEmail() != null) user.setEmail(request.getEmail());

        if (roleName != null) {
            Role role = roleRepo.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            user.getRoles().clear();
            user.getRoles().add(role);
        }

        return toResponse(userRepo.save(user));
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

    // ==========================
    // 6️⃣ CREATE STAFF
    // ==========================
    public UserResponse createStaff(StaffRequest request) {

        Role staffRole = roleRepo.findByName("ROLE_STAFF")
                .orElseThrow(() -> new RuntimeException("ROLE_STAFF not found"));

        User staff = new User();
        staff.setUsername(request.getUsername());
        staff.setPassword(encoder.encode(request.getPassword()));
        staff.setFullName(request.getFullName());
        staff.setPhone(request.getPhone());
        staff.setAddress(request.getAddress());
        staff.setEmail(request.getEmail());

        staff.getRoles().add(staffRole);

        staff.getManagedCategories().addAll(
                categoryRepo.findAllById(request.getCategoryIds())
        );

        return toResponse(userRepo.save(staff));
    }

    // ==========================
    // 7️⃣ UPDATE STAFF
    // ==========================
    @Transactional
    public UserResponse updateStaff(Long userId, StaffRequest request) {

        User staff = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1️⃣ đảm bảo ROLE_STAFF
        Role staffRole = roleRepo.findByName("ROLE_STAFF")
                .orElseThrow(() -> new RuntimeException("ROLE_STAFF not found"));
        if (!staff.getRoles().contains(staffRole)) {
            staff.getRoles().add(staffRole);
        }

        // 2️⃣ update username
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (!staff.getUsername().equals(request.getUsername())
                    && userRepo.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            staff.setUsername(request.getUsername());
        }

        // 3️⃣ update fullName
        if (request.getFullName() != null) {
            staff.setFullName(request.getFullName());
        }

        // 4️⃣ update phone
        if (request.getPhone() != null) {
            staff.setPhone(request.getPhone());
        }

        // 5️⃣ update address
        if (request.getAddress() != null) {
            staff.setAddress(request.getAddress());
        }

        // 6️⃣ update password (nếu có)
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            staff.setPassword(encoder.encode(request.getPassword()));
        }

        // 7️⃣ update managedCategories
        if (request.getCategoryIds() != null) {
            staff.getManagedCategories().clear();
            staff.getManagedCategories().addAll(
                    categoryRepo.findAllById(request.getCategoryIds())
            );
        }

        return toResponse(staff);
    }


}
