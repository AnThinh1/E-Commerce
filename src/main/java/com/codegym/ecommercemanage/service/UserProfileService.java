package com.codegym.ecommercemanage.service;

import com.codegym.ecommercemanage.dto.request.UpdateProfileRequest;
import com.codegym.ecommercemanage.dto.response.UserProfileResponse;
import com.codegym.ecommercemanage.model.Role;
import com.codegym.ecommercemanage.model.User;
import com.codegym.ecommercemanage.model.UserPrincipal;
import com.codegym.ecommercemanage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepo;

    // 📌 GET /api/users/me
    public UserProfileResponse getMyProfile() {
        User user = getCurrentUser();
        return map(user);
    }

    // 📌 PUT /api/users/me
    public UserProfileResponse updateMyProfile(UpdateProfileRequest r) {
        User user = getCurrentUser();

        user.setFullName(r.getFullName());
        user.setPhone(r.getPhone());
        user.setAddress(r.getAddress());
        user.setEmail(r.getEmail());

        userRepo.save(user);
        return map(user);
    }

    // ================= HELPER =================

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return userRepo.findByUsername(principal.getUsername());
    }

    private UserProfileResponse map(User u) {
        return UserProfileResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .fullName(u.getFullName())
                .phone(u.getPhone())
                .address(u.getAddress())
                .email(u.getEmail())
                .status(u.getStatus())
                .roles(
                        u.getRoles()
                                .stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet())
                )
                .build();
    }
}