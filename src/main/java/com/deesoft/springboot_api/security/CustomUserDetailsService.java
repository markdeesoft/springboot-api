package com.deesoft.springboot_api.security;

import com.deesoft.springboot_api.modules.user.entity.User;
import com.deesoft.springboot_api.modules.user.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    // 🟢 Custom Method 1: ดึง User Entity ตรงๆ
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    // 🟢 Custom Method 2: ดึงเฉพาะ User ID
    public Long getUserIdByUsername(String username) {
        return getUserEntityByUsername(username).getId();
    }

    // @Override
    // public UserResponse getMyProfileId() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    //     // 🟢 อ่าน ID จาก Principal Object ใน Memory ได้ทันที
    //     if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
    //         Long currentUserId = userDetails.getId();
    //         return userService.getUserById(currentUserId);
    //     }
    
    //     throw new UnauthorizedException("User authentication invalid");
    // }
}