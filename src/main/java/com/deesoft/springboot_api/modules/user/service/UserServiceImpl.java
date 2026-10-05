package com.deesoft.springboot_api.modules.user.service;

import com.deesoft.springboot_api.common.exception.ResourceNotFoundException;
import com.deesoft.springboot_api.modules.user.dto.*;
import com.deesoft.springboot_api.modules.user.entity.*;
import com.deesoft.springboot_api.modules.user.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Value("${app.password.default}")
    private String defaultPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserCreateRequest request) {

        // 🟢 1. ตรวจสอบ Username ซ้ำ
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken");
        }

        // 🟢 2. ตรวจสอบ Email ซ้ำ
        // if (userRepository.existsByEmail(request.getEmail())) {
        //     throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already in use");
        // }

        System.out.println(">>> Users careate: " + request);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode( defaultPassword ));
        user.setName(request.getName());
        user.setRole(Role.ROLE_USER);
        
        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    public Page<UserResponse> getAllUsers(int page,int size ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // List<User> users = userRepository.findAll(pageable);
        // System.out.println(">>> Users found in DB: " + users.size());
        Page<User> users = userRepository.findAll(pageable);
        System.out.println(">>> Users found in DB: " + users.getTotalElements());

        // List<UserResponse> responses = users.stream()
        //     .map(this::mapToResponse)
        //     .toList();
        Page<UserResponse> responses = users.map(this::mapToResponse);

        responses.forEach(r -> System.out.println(">>> DTO Data: " + r));

        return responses;
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {

            boolean isEmailTaken = userRepository.existsByEmailAndIdNot(request.getEmail(), id);
            if (isEmailTaken) {
                throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already in use by another user");
            }

            user.setEmail(request.getEmail());
        }

        // if (request.getPassword() != null && !request.getPassword().isBlank()) {
        //     user.setPassword(passwordEncoder.encode(request.getPassword()));
        // }

        Role targetRole = (request.getRole() != null) 
            ? request.getRole()
            : Role.ROLE_USER;

        user.setRole(targetRole); 

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
}