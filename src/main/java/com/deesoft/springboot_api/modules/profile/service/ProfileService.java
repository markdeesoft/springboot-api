package com.deesoft.springboot_api.modules.profile.service;

import com.deesoft.springboot_api.modules.profile.dto.ProfileResponse;
import com.deesoft.springboot_api.modules.profile.dto.ProfileUpdateRequest;

import java.util.List;

import org.springframework.data.domain.Page;

public interface ProfileService {
    ProfileResponse getProfile();
    ProfileResponse updateProfile(ProfileUpdateRequest request);
    ProfileResponse updatePassword(ProfileUpdateRequest request);
}