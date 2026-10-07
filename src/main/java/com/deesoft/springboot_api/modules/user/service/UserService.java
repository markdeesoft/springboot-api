package com.deesoft.springboot_api.modules.user.service;

import com.deesoft.springboot_api.modules.user.dto.UserCreateRequest;
import com.deesoft.springboot_api.modules.user.dto.UserResponse;
import com.deesoft.springboot_api.modules.user.dto.UserUpdateRequest;

import org.springframework.data.domain.Page;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    // List<UserResponse> getAllUsers();
    Page<UserResponse> getAllUsers(int page, int size);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
}