package com.deesoft.springboot_api.modules.profile.service;

import com.deesoft.springboot_api.modules.profile.dto.ProfileResponse;
import com.deesoft.springboot_api.modules.profile.dto.ProfileUpdateRequest;

public interface ProfileService {
    ProfileResponse getProfile();
    ProfileResponse updateProfile(ProfileUpdateRequest request);
    ProfileResponse resetPassword();
}