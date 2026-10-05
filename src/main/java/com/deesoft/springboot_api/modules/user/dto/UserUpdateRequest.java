package com.deesoft.springboot_api.modules.user.dto;

import com.deesoft.springboot_api.modules.user.entity.Role;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder // ใช้งาน Builder
public class UserUpdateRequest {

    private String name;
    // private String password;

    @Email (message = "Invalid email format") // 🟢 ตรวจสอบรูปแบบ Email อัตโนมัติ
    private String email;

    @Builder.Default
    private Role role = Role.ROLE_USER;
}