package com.skill.platform.auth.controller;

import com.skill.platform.auth.model.dto.ChangePasswordRequest;
import com.skill.platform.auth.model.dto.LoginRequest;
import com.skill.platform.auth.model.dto.LoginResponse;
import com.skill.platform.auth.model.dto.RefreshTokenRequest;
import com.skill.platform.auth.model.dto.RegisterRequest;
import com.skill.platform.auth.model.dto.UserInfoResponse;
import com.skill.platform.auth.service.AuthService;
import com.skill.platform.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success("Registration successful", null);
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refreshToken(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getCurrentUser() {
        return ApiResponse.success(authService.getCurrentUser());
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.success("Password changed successfully", null);
    }
}
