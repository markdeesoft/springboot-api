package com.deesoft.springboot_api.modules.user.entity;

import com.deesoft.springboot_api.common.entity.BaseEntity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity // 👈 ประกาศว่าคลาสนี้คือ Entity ให้ Hibernate นำไปสร้าง Table
@Table(name = "users")
@Getter @Setter
// 🟢 1. เปลี่ยนคำสั่ง deleteById() / delete() ให้เป็นการ Update deleted_at แทน
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
// 🟢 2. ดักทุก Query (findAll, findById) ให้กรองเอาเฉพาะแถวที่ deleted_at IS NULL (Hibernate 6.3+)
@SQLRestriction("deleted_at IS NULL")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String email;

    // @CreatedDate
    // @Column(name = "created_at", nullable = false, updatable = false)
    // private LocalDateTime createdAt;

    // @LastModifiedDate
    // @Column(name = "updated_at", nullable = false)
    // private LocalDateTime updatedAt;

    // @Column(name = "deleted_at")
    // private LocalDateTime deletedAt;

    // 🟢 เมธอด Helper สำหรับทำ Soft Delete แบบ Manual (ถ้าต้องการกำหนดเวลาเองใน Service)
    public void markAsDeleted() {
        this.setDeletedAt(LocalDateTime.now());
    }
}