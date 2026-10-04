package com.deesoft.springboot_api.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
// import lombok.Data;
import jakarta.validation.constraints.Size;

public class UserCreateRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 7, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    // @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format") // 🟢 ตรวจสอบรูปแบบ Email อัตโนมัติ
    private String email;

    // @NotBlank(message = "Password is required")
    // private String password;

    private String name;

    // --- Getter and Setter ---
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // public String getPassword() {
    //     return password;
    // }

    // public void setPassword(String password) {
    //     this.password = password;
    // }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}