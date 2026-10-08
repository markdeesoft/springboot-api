package com.deesoft.springboot_api.modules.profile.repository;

import com.deesoft.springboot_api.modules.profile.entity.Profile;
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
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByUsername - ควรหา Profile เจอเมื่อมี Username ในระบบ")
    void findByUsername_WhenExists_ShouldReturnProfile() {
        // Given
        Profile profile = Profile.builder()
                .username("john_doe")
                .email("john@example.com")
                .name("John Doe")
                .build();
        entityManager.persistAndFlush(profile);

        // When
        Optional<Profile> result = profileRepository.findByUsername("john_doe");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("john_doe");
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("findByUsername - ควรส่งกลับ Optional.empty() เมื่อไม่พบ Username")
    void findByUsername_WhenNotExists_ShouldReturnEmpty() {
        // When
        Optional<Profile> result = profileRepository.findByUsername("not_found");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("existsByUsername - ควรส่งกลับ true เมื่อ Username มีอยู่แล้ว")
    void existsByUsername_WhenExists_ShouldReturnTrue() {
        // Given
        Profile profile = Profile.builder()
                .username("existing_user")
                .email("existing@example.com")
                .name("Existing User")
                .build();
        entityManager.persistAndFlush(profile);

        // When
        boolean exists = profileRepository.existsByUsername("existing_user");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUsername - ควรส่งกลับ false เมื่อ Username ยังไม่มีในระบบ")
    void existsByUsername_WhenNotExists_ShouldReturnFalse() {
        // When
        boolean exists = profileRepository.existsByUsername("new_user");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByEmailAndIdNot - ควรส่งกลับ true เมื่อ Email ซ้ำกับ Profile ของผู้อื่น")
    void existsByEmailAndIdNot_WhenEmailIsTakenByOther_ShouldReturnTrue() {
        // Given
        Profile profile1 = Profile.builder()
                .username("user1")
                .email("duplicate@example.com")
                .name("User One")
                .build();
        Profile profile2 = Profile.builder()
                .username("user2")
                .email("other@example.com")
                .name("User Two")
                .build();

        entityManager.persistAndFlush(profile1);
        Profile savedProfile2 = entityManager.persistAndFlush(profile2);

        // When: เช็คว่า duplicate@example.com ซ้ำไหม โดยยกเว้น id ของ profile2
        boolean isTaken = profileRepository.existsByEmailAndIdNot("duplicate@example.com", savedProfile2.getId());

        // Then
        assertThat(isTaken).isTrue();
    }

    @Test
    @DisplayName("existsByEmailAndIdNot - ควรส่งกลับ false เมื่อใส่อีเมลเดิมของตนเอง")
    void existsByEmailAndIdNot_WhenEmailBelongsToSelf_ShouldReturnFalse() {
        // Given
        Profile profile = Profile.builder()
                .username("my_account")
                .email("myemail@example.com")
                .name("My Account")
                .build();
        Profile savedProfile = entityManager.persistAndFlush(profile);

        // When: เช็คอีเมลเดิมของตนเองโดยยกเว้น id ของตนเอง
        boolean isTaken = profileRepository.existsByEmailAndIdNot("myemail@example.com", savedProfile.getId());

        // Then
        assertThat(isTaken).isFalse();
    }
}