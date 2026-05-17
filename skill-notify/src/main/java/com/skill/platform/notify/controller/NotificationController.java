package com.skill.platform.notify.controller;

import com.skill.platform.common.response.ApiResponse;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.notify.model.dto.NotificationResponse;
import com.skill.platform.notify.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for notification operations (F012).
 * <p>
 * Endpoints:
 * <ul>
 *   <li>{@code GET    /api/v1/notifications}            - list notifications (paginated)</li>
 *   <li>{@code PUT    /api/v1/notifications/{id}/read}  - mark single notification as read</li>
 *   <li>{@code PUT    /api/v1/notifications/read-all}   - mark all as read</li>
 *   <li>{@code GET    /api/v1/notifications/unread-count} - get unread count</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * List the current user's notifications with pagination.
     *
     * @param unreadOnly optional filter; when true, return only unread notifications
     * @param pageable   pagination parameters (defaults: page=0, size=20)
     * @return paginated list of notifications
     */
    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse>> listNotifications(
            @RequestParam(required = false, defaultValue = "false") boolean unreadOnly,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/v1/notifications, unreadOnly={}, page={}, size={}",
                unreadOnly, pageable.getPageNumber(), pageable.getPageSize());
        PageResponse<NotificationResponse> notifications =
                notificationService.listNotifications(unreadOnly, pageable);
        return ApiResponse.success(notifications);
    }

    /**
     * Mark a single notification as read.
     *
     * @param id the notification ID
     * @return success response
     */
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable UUID id) {
        log.info("PUT /api/v1/notifications/{}/read", id);
        notificationService.markAsRead(id);
        return ApiResponse.success("Notification marked as read");
    }

    /**
     * Mark all of the current user's unread notifications as read.
     *
     * @return success response with the count of updated notifications
     */
    @PutMapping("/read-all")
    public ApiResponse<Map<String, Integer>> markAllAsRead() {
        log.info("PUT /api/v1/notifications/read-all");
        int count = notificationService.markAllAsRead();
        return ApiResponse.success(Map.of("updated", count));
    }

    /**
     * Get the unread notification count for the current user.
     *
     * @return the unread count
     */
    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> getUnreadCount() {
        log.info("GET /api/v1/notifications/unread-count");
        long count = notificationService.getUnreadCount();
        return ApiResponse.success(Map.of("count", count));
    }
}
