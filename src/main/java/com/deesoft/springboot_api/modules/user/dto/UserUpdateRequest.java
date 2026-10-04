package com.deesoft.springboot_api.modules.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    private String name;
    private Role role;
    // private String password;

    @Email(message = "Invalid email format") // 🟢 ตรวจสอบรูปแบบ Email อัตโนมัติ
    private String email;

    @Builder.Default
    private String role = "USER";
}