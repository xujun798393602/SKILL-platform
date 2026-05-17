package com.skill.platform.suite.model;

import com.skill.platform.core.model.Skill;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "suite_skills", uniqueConstraints = @UniqueConstraint(columnNames = {"suite_id", "skill_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuiteSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suite_id", nullable = false)
    private Suite suite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
