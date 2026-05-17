package com.skill.platform.stats.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the dashboard metrics response.
 * <p>
 * Aggregates 7 core platform metrics into a single response object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    /** Total number of skills registered on the platform. */
    private long totalSkills;

    /** Total number of registered users. */
    private long totalUsers;

    /** Total number of skill downloads across the platform. */
    private long totalDownloads;

    /** Total number of deployments executed. */
    private long totalDeployments;

    /** Number of skills uploaded today. */
    private long todayUploads;

    /** Number of users active today (based on login or download activity). */
    private long todayActiveUsers;

    /** Number of skills currently pending review. */
    private long pendingReviews;
}
