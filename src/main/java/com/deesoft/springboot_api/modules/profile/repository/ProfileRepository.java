package com.deesoft.springboot_api.modules.profile.repository;

import com.deesoft.springboot_api.modules.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    // ค้นหา User จาก Username (ใช้ตอนทำ Authentication/Login)
    Optional<Profile> findByUsername(String username);

    // validation
    // เช็คว่ามี Username นี้ในระบบแล้วหรือยัง 
    boolean existsByUsername(String username);
    boolean existsByEmailAndIdNot(String email, Long id);

}