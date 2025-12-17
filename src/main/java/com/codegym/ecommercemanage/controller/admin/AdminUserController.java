package com.codegym.ecommercemanage.controller.admin;

import com.codegym.ecommercemanage.dto.request.RegisterRequest;
import com.codegym.ecommercemanage.dto.response.UserResponse;
import com.codegym.ecommercemanage.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService service;

    // LIST
    @GetMapping
    public List<UserResponse> list() {
        return service.getAll();
    }

    // DETAIL  ✅ BỔ SUNG
    @GetMapping("/{id}")
    public UserResponse detail(@PathVariable Long id) {
        return service.getById(id);
    }

    // CREATE
    @PostMapping
    public UserResponse create(
            @RequestBody RegisterRequest r,
            @RequestParam String role
    ) {
        return service.createUser(r, role);
    }

    // UPDATE  ✅ role KHÔNG BẮT BUỘC
    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable Long id,
            @RequestBody RegisterRequest r,
            @RequestParam(required = false) String role
    ) {
        return service.updateUser(id, r, role);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // DEBUG (GIỮ LẠI)
    @GetMapping("/debug")
    public ResponseEntity<?> debug(Authentication authentication) {
        return ResponseEntity.ok(
                Map.of(
                        "name", authentication.getName(),
                        "authorities", authentication.getAuthorities(),
                        "class", authentication.getClass().getName()
                )
        );
    }
}



