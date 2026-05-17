package com.skill.platform.graph.model;

import com.skill.platform.core.model.Skill;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "position_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"position", "skill_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(nullable = false, length = 20)
    private String importance;

    @Column(length = 50)
    private String source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
