package com.deesoft.springboot_api.modules.auth.controller;

import com.deesoft.springboot_api.common.dto.ApiResponse;
import com.deesoft.springboot_api.modules.auth.dto.AuthResponse;
import com.deesoft.springboot_api.modules.auth.dto.LoginRequest;
import com.deesoft.springboot_api.modules.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        AuthResponse token = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", token));
    }
}