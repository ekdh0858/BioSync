package com.biosync.auth.controller;

import com.biosync.auth.dto.LoginRequest;
import com.biosync.auth.dto.LogoutRequest;
import com.biosync.auth.dto.RefreshRequest;
import com.biosync.auth.dto.SignUpRequest;
import com.biosync.auth.service.AuthService;
import com.biosync.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(authService.signup(request)));
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<?> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.success(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@Valid @RequestBody LogoutRequest request) {
        return ApiResponse.success(authService.logout(request));
    }
}
