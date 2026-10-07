package com.deesoft.springboot_api.modules.profile.service;

import com.deesoft.springboot_api.common.exception.ResourceNotFoundException;
import com.deesoft.springboot_api.security.CustomUserDetailsService;
import com.deesoft.springboot_api.modules.profile.dto.*;
import com.deesoft.springboot_api.modules.profile.entity.*;
import com.deesoft.springboot_api.modules.profile.repository.ProfileRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.core.context.SecurityContextHolder;


// import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Value("${app.password.default}")
    private String defaultPassword;

    // private final JwtService tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileServiceImpl(
        // JwtService tokenProvider, 
        CustomUserDetailsService userDetailsService,
        ProfileRepository profileRepository,
        PasswordEncoder passwordEncoder
    ) {
        // this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Long getUserDataID(){
        // String username = tokenProvider.getUsername(token);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userDetailsService.getUserIdByUsername(username);
        return userId;
    }

    public ProfileResponse getProfile() {

        Long currentUserId = getUserDataID();

        Profile user = profileRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + currentUserId));
        return mapToResponse(user);
    }

    private ProfileResponse mapToResponse(Profile user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public ProfileResponse updateProfile(ProfileUpdateRequest request) {
        
        Long currentUserId = getUserDataID();

        Profile user = profileRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + currentUserId));

        System.out.println(">>> User found in DB: " + user);

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {

            boolean isEmailTaken = profileRepository.existsByEmailAndIdNot(request.getEmail(), currentUserId);
            if (isEmailTaken) {
                throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already in use by another user");
            }

            user.setEmail(request.getEmail());
        }

        // if (request.getPassword() != null && !request.getPassword().isBlank()) {
        //     user.setPassword(passwordEncoder.encode(request.getPassword()));
        // }

        Profile updatedProfile = profileRepository.save(user);
        return mapToResponse(updatedProfile);
    }

    @Override
    public ProfileResponse resetPassword() {

        Long currentUserId = getUserDataID();

        Profile user = profileRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + currentUserId));

        user.setName(passwordEncoder.encode( defaultPassword ));

        Profile updatedProfile = profileRepository.save(user);
        return mapToResponse(updatedProfile);
        
    }
}