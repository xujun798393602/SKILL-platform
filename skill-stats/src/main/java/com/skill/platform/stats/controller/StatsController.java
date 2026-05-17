package com.skill.platform.stats.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.stats.model.dto.DashboardResponse;
import com.skill.platform.stats.model.dto.TrendResponse;
import com.skill.platform.stats.service.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for platform data statistics endpoints.
 * <p>
 * Provides dashboard metrics, trend analysis, and hot-skill rankings.
 */
@RestController
@RequestMapping("/api/v1/statistics")
@Slf4j
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * Get aggregated dashboard metrics.
     *
     * @return 7 core platform metrics: totalSkills, totalUsers, totalDownloads,
     *         totalDeployments, todayUploads, todayActiveUsers, pendingReviews
     */
    @GetMapping("/dashboard")
    public ApiResponse<DashboardResponse> getDashboard() {
        log.info("GET /api/v1/statistics/dashboard");
        DashboardResponse dashboard = statsService.getDashboard();
        return ApiResponse.success(dashboard);
    }

    /**
     * Get trend data for a specific metric over a date range.
     *
     * @param startDate start date in ISO-8601 format (yyyy-MM-dd)
     * @param endDate   end date in ISO-8601 format (yyyy-MM-dd)
     * @param metric    the metric to query: "skills", "downloads", or "users"
     * @return daily aggregated data points for the requested metric
     */
    @GetMapping("/trends")
    public ApiResponse<TrendResponse> getTrends(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "skills") String metric) {
        log.info("GET /api/v1/statistics/trends - startDate={}, endDate={}, metric={}", startDate, endDate, metric);
        TrendResponse trends = statsService.getTrends(startDate, endDate, metric);
        return ApiResponse.success(trends);
    }

    /**
     * Get the top hot skills ranked by download count.
     *
     * @param limit maximum number of skills to return (default 10)
     * @return list of top skills with download counts and ratings
     */
    @GetMapping("/hot-skills")
    public ApiResponse<List<Map<String, Object>>> getHotSkills(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("GET /api/v1/statistics/hot-skills - limit={}", limit);
        List<Map<String, Object>> hotSkills = statsService.getHotSkills(limit);
        return ApiResponse.success(hotSkills);
    }
}
