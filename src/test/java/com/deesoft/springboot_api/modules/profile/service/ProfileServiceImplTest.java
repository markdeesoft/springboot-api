package com.deesoft.springboot_api.modules.profile.service;

import com.deesoft.springboot_api.common.exception.ResourceNotFoundException;
import com.deesoft.springboot_api.modules.profile.dto.ProfileResponse;
import com.deesoft.springboot_api.modules.profile.dto.ProfileUpdateRequest;
import com.deesoft.springboot_api.modules.profile.entity.Profile;
import com.deesoft.springboot_api.modules.profile.repository.ProfileRepository;
import com.deesoft.springboot_api.security.CustomUserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private final String mockUsername = "testuser";
    private final Long mockUserId = 100L;

    @BeforeEach
    void setUp() {
        // กำหนดค่า @Value("${app.password.default}") เข้า Private Field
        ReflectionTestUtils.setField(profileService, "defaultPassword", "Default@1234");

        // Mock SecurityContextHolder
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn(mockUsername);
        SecurityContextHolder.setContext(securityContext);

        // Mock CustomUserDetailsService
        given(userDetailsService.getUserIdByUsername(mockUsername)).willReturn(mockUserId);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("getProfile Test Cases")
    class GetProfileTests {

        @Test
        @DisplayName("ดึงข้อมูล Profile สำเร็จเมื่อพบ ID ของ User ในระบบ")
        void getProfile_Success() {
            // Given
            Profile mockProfile = Profile.builder()
                    .id(mockUserId)
                    .username(mockUsername)
                    .name("Test User")
                    .role("USER")
                    .build();

            given(profileRepository.findById(mockUserId)).willReturn(Optional.of(mockProfile));

            // When
            ProfileResponse response = profileService.getProfile();

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(mockUserId);
            assertThat(response.getUsername()).isEqualTo(mockUsername);
            assertThat(response.getName()).isEqualTo("Test User");
            verify(profileRepository).findById(mockUserId);
        }

        @Test
        @DisplayName("โยน ResourceNotFoundException เมื่อไม่พบ Profile ตาม ID ในระบบ")
        void getProfile_NotFound_ThrowsException() {
            // Given
            given(profileRepository.findById(mockUserId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> profileService.getProfile())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Profile not found with id: " + mockUserId);
        }
    }

    @Nested
    @DisplayName("updateProfile Test Cases")
    class UpdateProfileTests {

        @Test
        @DisplayName("อัปเดต Name และ Email สำเร็จ")
        void updateProfile_Success() {
            // Given
            Profile existingProfile = Profile.builder()
                    .id(mockUserId)
                    .username(mockUsername)
                    .email("old@example.com")
                    .name("Old Name")
                    .build();

            ProfileUpdateRequest request = new ProfileUpdateRequest();
            request.setName("New Name");
            request.setEmail("new@example.com");

            given(profileRepository.findById(mockUserId)).willReturn(Optional.of(existingProfile));
            given(profileRepository.existsByEmailAndIdNot("new@example.com", mockUserId)).willReturn(false);
            given(profileRepository.save(any(Profile.class))).willAnswer(invocation -> invocation.getArgument(0));

            // When
            ProfileResponse response = profileService.updateProfile(request);

            // Then
            assertThat(response.getName()).isEqualTo("New Name");
            verify(profileRepository).save(existingProfile);
        }

        @Test
        @DisplayName("โยน IllegalArgumentException เมื่ออัปเดตไปใช้อีเมลที่ผู้อื่นใช้อยู่แล้ว")
        void updateProfile_DuplicateEmail_ThrowsException() {
            // Given
            Profile existingProfile = Profile.builder()
                    .id(mockUserId)
            
        .email("old@example.com")
                    .build();

            ProfileUpdateRequest request = new ProfileUpdateRequest();
            request.setEmail("taken@example.com");

            given(profileRepository.findById(mockUserId)).willReturn(Optional.of(existingProfile));
            given(profileRepository.existsByEmailAndIdNot("taken@example.com", mockUserId)).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> profileService.updateProfile(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already in use");

            verify(profileRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("resetPassword Test Cases")
    class ResetPasswordTests {

        @Test
        @DisplayName("Reset Password สำเร็จโดยใช้อัลกอริทึม PasswordEncoder เข้ารหัส defaultPassword")
        void resetPassword_Success() {
            // Given
            Profile existingProfile = Profile.builder()
                    .id(mockUserId)
                    .name("User")
                    .build();

            given(profileRepository.findById(mockUserId)).willReturn(Optional.of(existingProfile));
            given(passwordEncoder.encode("Default@1234")).willReturn("encoded_password");
            given(profileRepository.save(any(Profile.class))).willAnswer(invocation -> invocation.getArgument(0));

            // When
            ProfileResponse response = profileService.resetPassword();

            // Then
            assertThat(response).isNotNull();
            verify(passwordEncoder).encode("Default@1234");
            verify(profileRepository).save(existingProfile);
        }
    }
}