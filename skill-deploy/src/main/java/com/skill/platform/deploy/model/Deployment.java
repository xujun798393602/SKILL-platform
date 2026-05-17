package com.skill.platform.deploy.model;

import com.skill.platform.core.model.Skill;
import com.skill.platform.auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "deployments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deployment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(length = 20)
    private String version;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "endpoint", length = 500)
    private String endpoint;

    @Column(columnDefinition = "jsonb")
    private String config;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployed_by")
    private User deployedBy;

    @Column(name = "deployed_at")
    private Instant deployedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

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
