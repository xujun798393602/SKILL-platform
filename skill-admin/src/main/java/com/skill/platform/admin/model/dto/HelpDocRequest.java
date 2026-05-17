package com.skill.platform.admin.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a help document.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HelpDocRequest {

    /**
     * Document title. Required, max 200 characters.
     */
    @NotBlank(message = "Title must not be blank")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    /**
     * Document content in HTML or Markdown format. Required.
     */
    @NotBlank(message = "Content must not be blank")
    private String content;

    /**
     * Document type (e.g. FAQ, GUIDE, TUTORIAL). Max 50 characters.
     */
    @Size(max = 50, message = "Doc type must not exceed 50 characters")
    private String docType;

    /**
     * Document category for grouping. Max 100 characters.
     */
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;

    /**
     * Display sort order. Lower values appear first.
     */
    private Integer sortOrder;
}
