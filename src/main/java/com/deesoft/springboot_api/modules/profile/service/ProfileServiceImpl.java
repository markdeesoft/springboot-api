package com.deesoft.springboot_api.modules.profile.service;

import com.deesoft.springboot_api.common.exception.ResourceNotFoundException;
import com.deesoft.springboot_api.modules.profile.dto.*;
import com.deesoft.springboot_api.modules.profile.entity.*;
import com.deesoft.springboot_api.modules.profile.repository.ProfileRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Value("${app.password.default}")
    private String defaultPassword;

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileServiceImpl(ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ProfileResponse getProfile() {

        //mockup
        Long id = 3L;

        Profile user = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
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
        
        //mockup
        Long id = 3L;

        Profile user = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));

        System.out.println(">>> User found in DB: " + user);

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {

            boolean isEmailTaken = profileRepository.existsByEmailAndIdNot(request.getEmail(), id);
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
    public ProfileResponse updatePassword(ProfileUpdateRequest req) {
        return null;
    }
}