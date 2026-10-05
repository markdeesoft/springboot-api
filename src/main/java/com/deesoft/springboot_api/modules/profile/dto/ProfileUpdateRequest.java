package com.deesoft.springboot_api.modules.profile.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder // ใช้งาน Builder
@NoArgsConstructor // เจน Constructor เปล่าที่ Jackson ต้องการเพื่อแปลง JSON
@AllArgsConstructor // ช่วยเจน Constructor ที่รวมฟิลด์ทั้งหมด
public class ProfileUpdateRequest {

    private String name;
    // private String password;

    @Email (message = "Invalid email format") // 🟢 ตรวจสอบรูปแบบ Email อัตโนมัติ
    private String email;
}