package com.skill.platform.core.model;

import com.skill.platform.auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "skills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "skill_type", nullable = false, length = 50)
    private String skillType;

    @Column(length = 100)
    private String category;

    @Column(nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(length = 20)
    private String version;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(length = 64)
    private String checksum;

    @Column(name = "download_count")
    private Integer downloadCount;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @Column(name = "published_at")
    private Instant publishedAt;

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
