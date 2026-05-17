package com.skill.platform.admin.controller;

import com.skill.platform.admin.model.dto.LogResponse;
import com.skill.platform.admin.service.LogService;
import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.common.util.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for operation log management.
 * <p>
 * Provides endpoints for querying logs with filters and exporting logs.
 */
@RestController
@RequestMapping("/api/v1/logs")
@Slf4j
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * Query operation logs with optional filters and pagination.
     *
     * @param logType   optional log type filter
     * @param userId    optional user ID filter
     * @param startDate optional start of date range (ISO-8601 instant)
     * @param endDate   optional end of date range (ISO-8601 instant)
     * @param page      page number (default 1)
     * @param pageSize  page size (default 20)
     * @return paginated log responses
     */
    @GetMapping
    public ApiResponse<PageResponse<LogResponse>> listLogs(
            @RequestParam(required = false) String logType,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .sortBy("createdAt")
                .sortOrder("desc")
                .build();

        PageResponse<LogResponse> result = logService.queryLogs(logType, userId, startDate, endDate, pageRequest);
        return ApiResponse.success(result);
    }

    /**
     * Export operation logs.
     * <p>
     * Creates an export job and returns a download URL.
     *
     * @param logType   optional log type filter
     * @param userId    optional user ID filter
     * @param startDate optional start of date range
     * @param endDate   optional end of date range
     * @return download URL for the exported file
     */
    @PostMapping("/export")
    public ApiResponse<Map<String, String>> exportLogs(
            @RequestParam(required = false) String logType,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {

        String downloadUrl = logService.exportLogs(logType, userId, startDate, endDate);
        return ApiResponse.success(Map.of("downloadUrl", downloadUrl));
    }
}
