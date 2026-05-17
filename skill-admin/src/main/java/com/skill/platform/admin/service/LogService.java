package com.skill.platform.admin.service;

import com.skill.platform.admin.model.OperationLog;
import com.skill.platform.admin.model.dto.LogResponse;
import com.skill.platform.admin.repository.OperationLogRepository;
import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.common.util.PageRequest;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for operation log management.
 * <p>
 * Provides log querying with filters (log type, user, date range),
 * date range validation, and export functionality.
 */
@Service
@Slf4j
public class LogService {

    private static final int MAX_DATE_RANGE_DAYS = 90;

    private final OperationLogRepository operationLogRepository;

    public LogService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    /**
     * Query operation logs with optional filters and pagination.
     *
     * @param logType   optional log type filter
     * @param userId    optional user ID filter
     * @param startDate optional start of date range (ISO-8601)
     * @param endDate   optional end of date range (ISO-8601)
     * @param pageRequest pagination parameters
     * @return paginated log responses
     * @throws BusinessException if date range exceeds 90 days
     */
    public PageResponse<LogResponse> queryLogs(String logType, UUID userId,
                                                Instant startDate, Instant endDate,
                                                PageRequest pageRequest) {
        validateDateRange(startDate, endDate);

        Specification<OperationLog> spec = buildSpecification(logType, userId, startDate, endDate);

        var page = operationLogRepository.findAll(spec, pageRequest.toPageable());
        var responses = page.getContent().stream()
                .map(LogResponse::from)
                .toList();

        return PageResponse.of(page.getTotalElements(), page.getNumber() + 1,
                page.getSize(), responses);
    }

    /**
     * Export operation logs to a downloadable file.
     * <p>
     * Simplified implementation: returns a mock download URL.
     * In production, this would trigger an async export job.
     *
     * @param logType   optional log type filter
     * @param userId    optional user ID filter
     * @param startDate optional start of date range
     * @param endDate   optional end of date range
     * @return a download URL for the exported file
     * @throws BusinessException if date range exceeds 90 days
     */
    public String exportLogs(String logType, UUID userId,
                             Instant startDate, Instant endDate) {
        validateDateRange(startDate, endDate);

        String jobId = UUID.randomUUID().toString();
        log.info("Export job created: jobId={}, logType={}, userId={}, startDate={}, endDate={}",
                jobId, logType, userId, startDate, endDate);

        // Simplified: return a mock download URL
        // In production, this would create an async task and return the job ID
        return "/api/v1/logs/export/" + jobId + "/download";
    }

    /**
     * Validate that the date range does not exceed 90 days.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @throws BusinessException if the range exceeds 90 days
     */
    private void validateDateRange(Instant startDate, Instant endDate) {
        if (startDate != null && endDate != null) {
            long days = ChronoUnit.DAYS.between(startDate, endDate);
            if (days > MAX_DATE_RANGE_DAYS) {
                throw new BusinessException(ErrorCode.LOG002.getCode(),
                        "Date range must not exceed " + MAX_DATE_RANGE_DAYS + " days",
                        ErrorCode.LOG002.getHttpStatus());
            }
            if (days < 0) {
                throw new BusinessException(ErrorCode.LOG002.getCode(),
                        "Start date must be before end date",
                        ErrorCode.LOG002.getHttpStatus());
            }
        }
    }

    /**
     * Build a JPA {@link Specification} from the optional filter parameters.
     *
     * @param logType   optional log type
     * @param userId    optional user ID
     * @param startDate optional start date
     * @param endDate   optional end date
     * @return the combined specification
     */
    private Specification<OperationLog> buildSpecification(String logType, UUID userId,
                                                           Instant startDate, Instant endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (logType != null && !logType.isBlank()) {
                predicates.add(cb.equal(root.get("logType"), logType));
            }
            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
