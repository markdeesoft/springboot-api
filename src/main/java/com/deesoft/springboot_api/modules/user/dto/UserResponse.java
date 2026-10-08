package com.deesoft.springboot_api.modules.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor    // 🟢 สร้าง Default Constructor (จำเป็นสำหรับ Deserialization)
@AllArgsConstructor   // 🟢 สร้าง Constructor ที่รับพารามิเตอร์ครบทุก Field
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}