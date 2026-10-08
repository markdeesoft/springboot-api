package com.deesoft.springboot_api.modules.user.controller;

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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "deesoft_dev", roles = {"ADMIN"})
@Transactional // ย้อนกลับ (Rollback) ข้อมูลหลังทดสอบเสร็จในแต่ละเคส เพื่อไม่ให้ข้อมูลปนกัน
public class UserControllerTest {

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
    }

    @Test
    public void shouldCreateUserSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.username").value("deesoft_dev"));
    }

    @Test
    public void shouldGetAllUsersSuccessfully() throws Exception {

        userRepository.save(sampleUser); // บันทึกข้อมูลจำลองลง DB ก่อนเรียกใช้ GET ทั้งหมด

        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].username").value("deesoft_dev"));
    }

    @Test
    public void shouldGetUserByIdSuccessfully() throws Exception {
        User savedUser = userRepository.save(sampleUser);

        mockMvc.perform(get("/api/v1/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.data.username").value("deesoft_dev"));
    }

    @Test
    public void shouldUpdateUserSuccessfully() throws Exception {
        User savedUser = userRepository.save(sampleUser);

        // เตรียมข้อมูลชุดใหม่เพื่อส่งไปบันทึกทับข้อมูลเดิม
        User updatedDetails = new User();
        updatedDetails.setUsername("deesoft_newname");
        updatedDetails.setName("deesoft_dev_new");

        mockMvc.perform(put("/api/v1/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("deesoft_newname"))
                .andExpect(jsonPath("$.data.name").value("deesoft_dev_new"));
    }

    @Test
    public void shouldDeleteUserSuccessfully() throws Exception {
        User savedUser = userRepository.save(sampleUser);

        mockMvc.perform(delete("/api/v1/users/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        // ดักเช็กใน Repository อีกครั้งว่าถูกลบไปแล้วจริงๆ
        assert(userRepository.findById(savedUser.getId()).isEmpty());
    }
}
