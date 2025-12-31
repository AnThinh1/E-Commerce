package com.codegym.ecommercemanage.controller;

import com.codegym.ecommercemanage.dto.request.UpdateProfileRequest;
import com.codegym.ecommercemanage.dto.response.UserProfileResponse;
import com.codegym.ecommercemanage.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserProfileController {

    private final UserProfileService service;

    @GetMapping("/me")
    public UserProfileResponse me() {
        return service.getMyProfile();
    }

    @PutMapping("/me")
    public UserProfileResponse update(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return service.updateMyProfile(request);
    }
}