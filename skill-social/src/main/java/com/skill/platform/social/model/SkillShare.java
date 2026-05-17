package com.skill.platform.social.model;

import com.skill.platform.auth.model.User;
import com.skill.platform.core.model.Skill;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "skill_shares")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillShare {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "share_token", unique = true, nullable = false, length = 64)
    private String shareToken;

    @Column(name = "share_type", nullable = false, length = 20)
    private String shareType;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "access_count")
    private Integer accessCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
