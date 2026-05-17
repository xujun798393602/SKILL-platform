package com.skill.platform.common.util;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Reusable pagination request parameters.
 * <p>
 * Defaults to page 1, 20 items per page, sorted by {@code createdAt}
 * descending. Call {@link #toPageable()} to convert into a Spring Data
 * {@link Pageable} for repository queries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    @Min(value = 1, message = "Page number must be >= 1")
    @Builder.Default
    private int page = 1;

    @Min(value = 1, message = "Page size must be >= 1")
    @Max(value = 100, message = "Page size must be <= 100")
    @Builder.Default
    private int pageSize = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Pattern(regexp = "^(?i)(asc|desc)$", message = "Sort order must be 'asc' or 'desc'")
    @Builder.Default
    private String sortOrder = "desc";

    /**
     * Convert this request into a Spring Data {@link Pageable}.
     * <p>
     * Spring Data uses 0-based page indices, so this method subtracts 1
     * from the {@code page} field.
     *
     * @return a Pageable instance matching the request parameters
     */
    public Pageable toPageable() {
        Sort sort = "asc".equalsIgnoreCase(sortOrder)
                ? Sort.by(Sort.Direction.ASC, sortBy)
                : Sort.by(Sort.Direction.DESC, sortBy);
        return org.springframework.data.domain.PageRequest.of(page - 1, pageSize, sort);
    }
}
