package com.deesoft.springboot_api.modules.user.service;

import com.deesoft.springboot_api.common.exception.ResourceNotFoundException;
import com.deesoft.springboot_api.modules.user.dto.UserResponse;
import com.deesoft.springboot_api.modules.user.dto.UserUpdateRequest;
import com.deesoft.springboot_api.modules.user.entity.Role;
import com.deesoft.springboot_api.modules.user.entity.User;
import com.deesoft.springboot_api.modules.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("getUserById Test Cases")
    class GetUserByIdTests {

        @Test
        @DisplayName("เมื่อหา User เจอตาม ID ควรคืนค่า UserResponse ได้ถูกต้อง")
        void getUserById_Success() {
            // Given
            Long userId = 1L;
            User mockUser = User.builder()
                    .id(userId)
                    .username("john_doe")
                    .email("john@example.com")
                    .name("John Doe")
                    .role( Role.ROLE_USER )
                    .build();

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

            // When
            UserResponse response = userService.getUserById(userId);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(userId);
            assertThat(response.getUsername()).isEqualTo("john_doe");
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("เมื่อหา User ไม่เจอตาม ID ควรโยน ResourceNotFoundException")
        void getUserById_NotFound_ThrowsException() {
            // Given
            Long userId = 999L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.getUserById(userId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id: 999");
        }
    }

    @Nested
    @DisplayName("updateUser Test Cases")
    class UpdateUserTests {

        @Test
        @DisplayName("อัปเดตข้อมูลสำเร็จ และกำหนด Default Role เป็น USER เมื่อไม่ได้ส่ง role มา")
        void updateUser_Success_WithDefaultRole() {
            // Given
            Long userId = 1L;
            User existingUser = User.builder()
                    .id(userId)
                    .username("john_doe")
                    .email("john@example.com")
                    .name("John Old")
                    .role( Role.ROLE_USER )
                    .build();

            UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                    .name("John New")
                    .email("john_new@example.com")
                    .role(null) // ไม่ส่ง role มา -> ควรได้ "ROLE_USER"
                    .build();

            given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByEmailAndIdNot("john_new@example.com", userId)).willReturn(false);
            given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

            // When
            UserResponse response = userService.updateUser(userId, updateRequest);

            // Then
            assertThat(response.getName()).isEqualTo("John New");
            assertThat(response.getEmail()).isEqualTo("john_new@example.com");
            assertThat(response.getRole()).isEqualTo("ROLE_USER");
            verify(userRepository).save(existingUser);
        }

        @Test
        @DisplayName("เมื่อส่ง Email ใหม่ที่ซ้ำกับผู้อื่น ควรโยน IllegalArgumentException")
        void updateUser_DuplicateEmail_ThrowsException() {
            // Given
            Long userId = 1L;
            User existingUser = User.builder()
                    .id(userId)
                    .username("john_doe")
                    .email("john@example.com")
                    .build();

            UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                    .email("taken@example.com")
                    .build();

            given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
            given(userRepository.existsByEmailAndIdNot("taken@example.com", userId)).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.updateUser(userId, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already in use");

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("deleteUser (Soft Delete) Test Cases")
    class DeleteUserTests {

        @Test
        @DisplayName("ลบ User สำเร็จ (เรียก delete() ซึ่ง Hibernate จะแปลงเป็น Soft Delete)")
        void deleteUser_Success() {
            // Given
            Long userId = 1L;
            User existingUser = User.builder().id(userId).username("john_doe").build();

            given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));

            // When
            userService.deleteUser(userId);

            // Then
            verify(userRepository).delete(existingUser);
        }

        @Test
        @DisplayName("ลบ User ไม่สำเร็จเมื่อไม่พบ ID ในระบบ")
        void deleteUser_NotFound_ThrowsException() {
            // Given
            Long userId = 999L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.deleteUser(userId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository, never()).delete(any());
        }
    }
}