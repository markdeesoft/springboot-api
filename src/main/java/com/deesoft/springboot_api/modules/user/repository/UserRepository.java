package com.deesoft.springboot_api.modules.user.repository;

import com.deesoft.springboot_api.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ค้นหา User จาก Username (ใช้ตอนทำ Authentication/Login)
    Optional<User> findByUsername(String username);

    // validation
    // เช็คว่ามี Username นี้ในระบบแล้วหรือยัง (ใช้ตอน Register / Create User)
    boolean existsByUsername(String username);
    // boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    // Page<User> findByNameContaining(String name, Pageable pageable);

}