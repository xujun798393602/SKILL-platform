package com.skill.platform.stats.service;

import com.skill.platform.auth.repository.UserRepository;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.core.model.Skill;
import com.skill.platform.core.repository.DownloadLogRepository;
import com.skill.platform.core.repository.SkillRepository;
import com.skill.platform.deploy.repository.DeploymentRepository;
import com.skill.platform.social.repository.SkillReviewRepository;
import com.skill.platform.stats.model.dto.DashboardResponse;
import com.skill.platform.stats.model.dto.TrendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service providing platform-wide data statistics.
 * <p>
 * Aggregates data from skill, user, download, deployment, and review
 * repositories to produce dashboard metrics, trend analysis, and
 * hot-skill rankings.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class StatsService {

    private static final String SKILL_STATUS_PENDING = "PENDING";
    private static final String SKILL_STATUS_PUBLISHED = "PUBLISHED";
    private static final String DEPLOYMENT_STATUS_PENDING = "PENDING";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final DownloadLogRepository downloadLogRepository;
    private final DeploymentRepository deploymentRepository;
    private final SkillReviewRepository skillReviewRepository;

    public StatsService(SkillRepository skillRepository,
                        UserRepository userRepository,
                        DownloadLogRepository downloadLogRepository,
                        DeploymentRepository deploymentRepository,
                        SkillReviewRepository skillReviewRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.downloadLogRepository = downloadLogRepository;
        this.deploymentRepository = deploymentRepository;
        this.skillReviewRepository = skillReviewRepository;
    }

    /**
     * Aggregate the 7 core dashboard metrics.
     *
     * @return a DashboardResponse containing all dashboard metrics
     */
    public DashboardResponse getDashboard() {
        log.info("Aggregating dashboard metrics");

        long totalSkills = skillRepository.count();
        long totalUsers = userRepository.count();
        long totalDownloads = downloadLogRepository.count();
        long totalDeployments = deploymentRepository.count();

        // Today's boundaries in UTC
        Instant todayStart = LocalDate.now(ZoneOffset.UTC).atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant todayEnd = todayStart.plus(java.time.Duration.ofDays(1));

        // Count skills uploaded today by fetching published/pending skills and filtering in-memory
        long todayUploads = countSkillsCreatedBetween(todayStart, todayEnd);

        // Count active users today: users who have a login or download record today
        // Approximate via download logs created today
        long todayActiveUsers = countDownloadLogsCreatedBetween(todayStart, todayEnd);

        // Pending reviews: skills in PENDING status awaiting review
        long pendingReviews = skillRepository.countByStatus(SKILL_STATUS_PENDING);

        log.info("Dashboard metrics: totalSkills={}, totalUsers={}, totalDownloads={}, totalDeployments={}, todayUploads={}, todayActiveUsers={}, pendingReviews={}",
                totalSkills, totalUsers, totalDownloads, totalDeployments, todayUploads, todayActiveUsers, pendingReviews);

        return DashboardResponse.builder()
                .totalSkills(totalSkills)
                .totalUsers(totalUsers)
                .totalDownloads(totalDownloads)
                .totalDeployments(totalDeployments)
                .todayUploads(todayUploads)
                .todayActiveUsers(todayActiveUsers)
                .pendingReviews(pendingReviews)
                .build();
    }

    /**
     * Query trend data for a specific metric over a date range.
     *
     * @param startDate the start date (ISO-8601, e.g. "2026-05-01")
     * @param endDate   the end date (ISO-8601, e.g. "2026-05-17")
     * @param metric    the metric to query: "skills", "downloads", or "users"
     * @return a TrendResponse with daily aggregated data points
     */
    public TrendResponse getTrends(String startDate, String endDate, String metric) {
        log.info("Querying trend data: startDate={}, endDate={}, metric={}", startDate, endDate, metric);

        LocalDate start = parseDate(startDate, "startDate");
        LocalDate end = parseDate(endDate, "endDate");

        if (start.isAfter(end)) {
            throw new BusinessException(ErrorCode.VALIDATION001.getCode(),
                    "startDate must not be after endDate", ErrorCode.VALIDATION001.getHttpStatus());
        }

        Instant startInstant = start.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endInstant = end.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<TrendResponse.DailyDataPoint> dataPoints;

        switch (metric.toLowerCase()) {
            case "skills":
                dataPoints = buildSkillTrend(start, end, startInstant, endInstant);
                break;
            case "downloads":
                dataPoints = buildDownloadTrend(start, end, startInstant, endInstant);
                break;
            case "users":
                // Users trend is approximated via download activity (no direct user-created-at range query available)
                dataPoints = buildDownloadTrend(start, end, startInstant, endInstant);
                break;
            default:
                throw new BusinessException(ErrorCode.VALIDATION001.getCode(),
                        "Unsupported metric: " + metric + ". Supported: skills, downloads, users",
                        ErrorCode.VALIDATION001.getHttpStatus());
        }

        return TrendResponse.builder()
                .metric(metric)
                .startDate(startDate)
                .endDate(endDate)
                .dataPoints(dataPoints)
                .build();
    }

    /**
     * Retrieve the top skills ranked by download count or average rating.
     *
     * @param limit maximum number of skills to return (default 10)
     * @return list of top skills as maps with name, downloadCount, avgRating, etc.
     */
    public List<Map<String, Object>> getHotSkills(int limit) {
        log.info("Fetching top {} hot skills by downloads", limit);

        if (limit <= 0) {
            limit = 10;
        }

        // Fetch published skills sorted by download count descending
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "downloadCount"));
        List<Skill> topByDownloads = skillRepository.findByStatus(SKILL_STATUS_PUBLISHED, pageRequest).getContent();

        return topByDownloads.stream()
                .map(skill -> {
                    Map<String, Object> map = new java.util.LinkedHashMap<>();
                    map.put("id", skill.getId());
                    map.put("name", skill.getName());
                    map.put("skillType", skill.getSkillType());
                    map.put("category", skill.getCategory());
                    map.put("downloadCount", skill.getDownloadCount() != null ? skill.getDownloadCount() : 0);
                    map.put("avgRating", skill.getAvgRating() != null ? skill.getAvgRating() : BigDecimal.ZERO);
                    map.put("ratingCount", skill.getRatingCount() != null ? skill.getRatingCount() : 0);
                    map.put("ownerName", skill.getOwner() != null ? skill.getOwner().getName() : null);
                    return map;
                })
                .collect(Collectors.toList());
    }

    // ---- Private helpers ----

    /**
     * Count skills created between two instants by fetching candidates and filtering in-memory.
     * This is necessary because SkillRepository does not expose a findByCreatedAtBetween method.
     */
    private long countSkillsCreatedBetween(Instant start, Instant end) {
        try {
            // Use Specification-like approach: fetch all and filter
            // For production, a custom repository method would be more efficient
            return skillRepository.findAll().stream()
                    .filter(s -> s.getCreatedAt() != null
                            && !s.getCreatedAt().isBefore(start)
                            && s.getCreatedAt().isBefore(end))
                    .count();
        } catch (Exception e) {
            log.warn("Failed to count skills created between {} and {}: {}", start, end, e.getMessage());
            return 0;
        }
    }

    /**
     * Count download log entries created between two instants.
     * Fetches all and filters in-memory since DownloadLogRepository lacks findByCreatedAtBetween.
     */
    private long countDownloadLogsCreatedBetween(Instant start, Instant end) {
        try {
            return downloadLogRepository.findAll().stream()
                    .filter(d -> d.getCreatedAt() != null
                            && !d.getCreatedAt().isBefore(start)
                            && d.getCreatedAt().isBefore(end))
                    .count();
        } catch (Exception e) {
            log.warn("Failed to count download logs between {} and {}: {}", start, end, e.getMessage());
            return 0;
        }
    }

    /**
     * Build daily data points for the "skills" metric over the given date range.
     */
    private List<TrendResponse.DailyDataPoint> buildSkillTrend(LocalDate start, LocalDate end,
                                                                Instant startInstant, Instant endInstant) {
        // Fetch all skills created in the range (broad fetch + filter)
        List<Skill> skillsInRange;
        try {
            skillsInRange = skillRepository.findAll().stream()
                    .filter(s -> s.getCreatedAt() != null
                            && !s.getCreatedAt().isBefore(startInstant)
                            && s.getCreatedAt().isBefore(endInstant))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Failed to fetch skills for trend: {}", e.getMessage());
            skillsInRange = List.of();
        }

        // Group by date
        Map<LocalDate, Long> countByDate = skillsInRange.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate(),
                        Collectors.counting()
                ));

        return buildDailyDataPoints(start, end, countByDate);
    }

    /**
     * Build daily data points for the "downloads" metric over the given date range.
     */
    private List<TrendResponse.DailyDataPoint> buildDownloadTrend(LocalDate start, LocalDate end,
                                                                   Instant startInstant, Instant endInstant) {
        try {
            var downloadsInRange = downloadLogRepository.findAll().stream()
                    .filter(d -> d.getCreatedAt() != null
                            && !d.getCreatedAt().isBefore(startInstant)
                            && d.getCreatedAt().isBefore(endInstant))
                    .collect(Collectors.toList());

            Map<LocalDate, Long> countByDate = downloadsInRange.stream()
                    .collect(Collectors.groupingBy(
                            d -> d.getCreatedAt().atZone(ZoneOffset.UTC).toLocalDate(),
                            Collectors.counting()
                    ));

            return buildDailyDataPoints(start, end, countByDate);
        } catch (Exception e) {
            log.warn("Failed to fetch downloads for trend: {}", e.getMessage());
            return buildDailyDataPoints(start, end, Map.of());
        }
    }

    /**
     * Fill in all dates in the range, inserting zero for dates with no data.
     */
    private List<TrendResponse.DailyDataPoint> buildDailyDataPoints(LocalDate start, LocalDate end,
                                                                     Map<LocalDate, Long> countByDate) {
        List<TrendResponse.DailyDataPoint> points = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            long value = countByDate.getOrDefault(current, 0L);
            points.add(TrendResponse.DailyDataPoint.builder()
                    .date(current.format(DATE_FORMATTER))
                    .value(value)
                    .build());
            current = current.plusDays(1);
        }
        return points;
    }

    /**
     * Parse an ISO-8601 date string into a LocalDate.
     */
    private LocalDate parseDate(String dateStr, String fieldName) {
        if (dateStr == null || dateStr.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION001.getCode(),
                    fieldName + " must not be blank", ErrorCode.VALIDATION001.getHttpStatus());
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorCode.VALIDATION001.getCode(),
                    "Invalid date format for " + fieldName + ": " + dateStr + ". Expected ISO-8601 (yyyy-MM-dd)",
                    ErrorCode.VALIDATION001.getHttpStatus());
        }
    }
}
