package com.deesoft.springboot_api.modules.profile.controller;

import com.deesoft.springboot_api.common.dto.ApiResponse;
import com.deesoft.springboot_api.modules.profile.dto.*;
import com.deesoft.springboot_api.modules.profile.service.ProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile() {
        return ResponseEntity.ok(ApiResponse.success("Profiles retrieved", profileService.getProfile()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(@RequestBody ProfileUpdateRequest req) {
        ProfileResponse response = profileService.updateProfile(req);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PatchMapping ("/resetpass")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile() {
        ProfileResponse response = profileService.resetPassword();
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", response));
    }
}