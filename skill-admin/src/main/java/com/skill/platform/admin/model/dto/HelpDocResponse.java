package com.skill.platform.admin.model.dto;

import com.skill.platform.admin.model.HelpDoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for help document items.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpDocResponse {

    private UUID id;
    private String title;
    private String content;
    private String docType;
    private String category;
    private Integer sortOrder;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Convert a {@link HelpDoc} entity to a HelpDocResponse.
     *
     * @param entity the HelpDoc entity
     * @return the HelpDocResponse DTO
     */
    public static HelpDocResponse fromEntity(HelpDoc entity) {
        return HelpDocResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .docType(entity.getDocType())
                .category(entity.getCategory())
                .sortOrder(entity.getSortOrder())
                .createdBy(entity.getCreatedBy() != null ? entity.getCreatedBy().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
