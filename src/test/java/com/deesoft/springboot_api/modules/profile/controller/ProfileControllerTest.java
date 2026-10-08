package com.deesoft.springboot_api.modules.profile.controller;

import com.deesoft.springboot_api.modules.user.entity.Role;
import com.deesoft.springboot_api.modules.user.entity.User;
import com.deesoft.springboot_api.modules.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "deesoft_dev", roles = {"USER"})
@Transactional // ย้อนกลับ (Rollback) ข้อมูลหลังทดสอบเสร็จในแต่ละเคส เพื่อไม่ให้ข้อมูลปนกัน
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper; // ช่วยแปลง Java Object เป็น JSON String

    private User sampleUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // เคลียร์ฐานข้อมูลก่อนเริ่มทดสอบแต่ละ Method

        sampleUser = new User();
        sampleUser.setUsername("deesoft_dev");
        sampleUser.setPassword("password123");
        sampleUser.setName("deesoft_dev");
        sampleUser.setRole(Role.ROLE_USER);

        userRepository.save(sampleUser);
    }

    @Test
    public void shouldGetUserProfileSuccessfully() throws Exception {
        User savedUser = userRepository.save(sampleUser);

        mockMvc.perform(get("/api/v1/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.data.username").value("deesoft_dev"));
    }

    @Test
    public void shouldUpdateUserSuccessfully() throws Exception {

        userRepository.save(sampleUser);

        // เตรียมข้อมูลชุดใหม่เพื่อส่งไปบันทึกทับข้อมูลเดิม
        User updatedDetails = new User();
        updatedDetails.setName("deesoft_dev_new");

        mockMvc.perform(put("/api/v1/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("deesoft_dev_new"));
    }

    @Test
    public void shouldResetPasswordSuccessfully() throws Exception {

        userRepository.save(sampleUser);

        User updatedDetails = new User();
        updatedDetails.setPassword( "reset_password123" );

        mockMvc.perform(patch("/api/v1/profile/resetpass")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successfully"));

        // 🟢 ตรวจสอบรหัสผ่านใหม่จาก Database โดยตรง
        // User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();
        // assertThat(updatedUser.getPassword()).isEqualTo("reset_password123"); // หากมีการ encode รหัสผ่าน ให้ใช้ passwordEncoder.matches("reset_password123", updatedUsergetPassword())
    }
}
