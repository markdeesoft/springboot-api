package com.deesoft.springboot_api.modules.auth.service;

import com.deesoft.springboot_api.modules.auth.dto.AuthResponse;
import com.deesoft.springboot_api.modules.auth.dto.LoginRequest;
import com.deesoft.springboot_api.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService tokenProvider;

    public AuthService(AuthenticationManager authenticationManager, JwtService tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = tokenProvider.generateToken(authentication);
        return new AuthResponse(token);
    }
}