package com.skill.platform.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id", unique = true, nullable = false, length = 50)
    private String employeeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 200)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "login_fail_count")
    private Integer loginFailCount;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
