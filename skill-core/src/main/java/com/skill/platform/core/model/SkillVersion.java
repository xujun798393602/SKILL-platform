package com.skill.platform.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "skill_versions", uniqueConstraints = @UniqueConstraint(columnNames = {"skill_id", "version"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false, length = 20)
    private String version;

    @Column(columnDefinition = "TEXT")
    private String changelog;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(length = 64)
    private String checksum;

    @Column(length = 50)
    private String tag;

    @Column(name = "is_active")
    private Boolean isActive;

    @Version
    private Long optLock;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
