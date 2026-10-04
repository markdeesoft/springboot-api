package com.deesoft.springboot_api.config;

import com.deesoft.springboot_api.modules.user.entity.Role;
import com.deesoft.springboot_api.modules.user.entity.User;
import com.deesoft.springboot_api.modules.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // เช็คก่อนว่ามีข้อมูล Admin อยู่ในระบบหรือยัง เพื่อไม่ให้สร้างซ้ำ
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // เข้ารหัสผ่านก่อนบันทึก
            admin.setName("System Administrator");
            admin.setRole(Role.ROLE_ADMIN);

            userRepository.save(admin);
            System.out.println("✅ DataSeeder: Default admin user created successfully.");
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User defaultUser = new User();
            defaultUser.setUsername("user");
            defaultUser.setPassword(passwordEncoder.encode("user123"));
            defaultUser.setName("Normal User");
            defaultUser.setRole(Role.ROLE_USER);

            userRepository.save(defaultUser);
            System.out.println("✅ DataSeeder: Default normal user created successfully.");
        }
    }
}