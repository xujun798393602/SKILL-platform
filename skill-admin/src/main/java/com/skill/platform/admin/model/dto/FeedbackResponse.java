package com.skill.platform.admin.model.dto;

import com.skill.platform.admin.model.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for feedback details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    private UUID id;
    private UUID userId;
    private String userName;
    private String title;
    private String content;
    private String category;
    private String status;
    private String reply;
    private UUID repliedBy;
    private String repliedByName;
    private Instant repliedAt;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Convert a {@link Feedback} entity to a FeedbackResponse DTO.
     *
     * @param entity the Feedback entity
     * @return the FeedbackResponse DTO
     */
    public static FeedbackResponse fromEntity(Feedback entity) {
        return FeedbackResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .userName(entity.getUser() != null ? entity.getUser().getName() : null)
                .title(entity.getTitle())
                .content(entity.getContent())
                .category(entity.getCategory())
                .status(entity.getStatus())
                .reply(entity.getReply())
                .repliedBy(entity.getRepliedBy() != null ? entity.getRepliedBy().getId() : null)
                .repliedByName(entity.getRepliedBy() != null ? entity.getRepliedBy().getName() : null)
                .repliedAt(entity.getRepliedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
