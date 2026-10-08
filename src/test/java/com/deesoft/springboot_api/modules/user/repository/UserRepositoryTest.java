package com.deesoft.springboot_api.modules.user.repository;

import com.deesoft.springboot_api.modules.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // ใช้ application-test.properties (ถ้ามี)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // ให้ใช้ DB ตาม Config ของ Test
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager; // ใช้ช่วย Insert ข้อมูลทดสอบลง DB

    @Test
    @DisplayName("findByUsername - ควรเจอ User เมื่อมี username ตรงในระบบ")
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Given
        User user = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .name("John Doe")
                .build();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> foundUser = userRepository.findByUsername("john_doe");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("john_doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("findByUsername - ควรได้ Optional.empty() เมื่อไม่มี username ในระบบ")
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<User> foundUser = userRepository.findByUsername("unknown_user");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("existsByUsername - ควรส่งกลับ true เมื่อ username มีอยู่แล้ว")
    void existsByUsername_WhenUsernameExists_ShouldReturnTrue() {
        // Given
        User user = User.builder()
                .username("existing_user")
                .email("existing@example.com")
                .name("Existing User")
                .build();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByUsername("existing_user");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUsername - ควรส่งกลับ false เมื่อ username ยังไม่มีในระบบ")
    void existsByUsername_WhenUsernameDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByUsername("new_user");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByEmailAndIdNot - ควรส่งกลับ true เมื่ออีเมลซ้ำกับ User คนอื่น")
    void existsByEmailAndIdNot_WhenEmailIsUsedByAnotherUser_ShouldReturnTrue() {
        // Given
        User user1 = User.builder()
                .username("user1")
                .email("duplicate@example.com")
                .name("User One")
                .build();
        User user2 = User.builder()
                .username("user2")
                .email("other@example.com")
                .name("User Two")
                .build();

        User savedUser1 = entityManager.persistAndFlush(user1);
        User savedUser2 = entityManager.persistAndFlush(user2);

        // When: เช็คว่าอีเมลของ user1 ถูกใช้โดยคนอื่นหรือเปล่า โดยละเว้น id ของ user2
        boolean isTaken = userRepository.existsByEmailAndIdNot("duplicate@example.com", savedUser2.getId());

        // Then
        assertThat(isTaken).isTrue();
    }

    @Test
    @DisplayName("existsByEmailAndIdNot - ควรส่งกลับ false เมื่อใส่อีเมลเดิมของตัวเอง")
    void existsByEmailAndIdNot_WhenEmailBelongsToSameUser_ShouldReturnFalse() {
        // Given
        User user = User.builder()
                .username("my_account")
                .email("myemail@example.com")
                .name("My Account")
                .build();
        User savedUser = entityManager.persistAndFlush(user);

        // When: เช็คว่าอีเมลเดิมซ้ำไหม โดยใส่ id ของตัวเองเข้าไปละเว้น
        boolean isTaken = userRepository.existsByEmailAndIdNot("myemail@example.com", savedUser.getId());

        // Then
        assertThat(isTaken).isFalse();
    }
}