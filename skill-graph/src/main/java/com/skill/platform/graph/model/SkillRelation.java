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
@Table(name = "skill_relations", uniqueConstraints = @UniqueConstraint(columnNames = {"source_skill_id", "target_skill_id", "relation_type"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_skill_id", nullable = false)
    private Skill sourceSkill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_skill_id", nullable = false)
    private Skill targetSkill;

    @Column(name = "relation_type", nullable = false, length = 50)
    private String relationType;

    @Column(length = 200)
    private String label;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
