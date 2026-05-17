package com.skill.platform.notify.service;

import com.skill.platform.common.exception.BusinessException;
import com.skill.platform.common.exception.ErrorCode;
import com.skill.platform.common.response.PageResponse;
import com.skill.platform.common.util.UserContext;
import com.skill.platform.notify.model.Notification;
import com.skill.platform.notify.model.dto.NotificationResponse;
import com.skill.platform.notify.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service layer for notification operations (F012).
 * <p>
 * Supports listing notifications, marking a single notification as read,
 * and batch marking all notifications as read for the current user.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * List the current user's notifications with pagination.
     *
     * @param unreadOnly if true, return only unread notifications; otherwise return all
     * @param pageable   pagination parameters
     * @return a paginated list of {@link NotificationResponse} entries
     */
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> listNotifications(boolean unreadOnly, Pageable pageable) {
        UUID userId = getCurrentUserId();

        Page<Notification> page;
        if (unreadOnly) {
            page = notificationRepository.findByUserIdAndIsRead(userId, false, pageable);
        } else {
            page = notificationRepository.findByUserId(userId, pageable);
        }

        log.debug("User {} listing notifications, unreadOnly={}, page={}, total={}",
                userId, unreadOnly, page.getNumber(), page.getTotalElements());
        return PageResponse.of(page.map(NotificationResponse::from));
    }

    /**
     * Mark a single notification as read.
     *
     * @param notificationId the ID of the notification to mark as read
     * @throws BusinessException if the notification does not exist or does not belong to the current user
     */
    @Transactional
    public void markAsRead(UUID notificationId) {
        UUID userId = getCurrentUserId();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFY001));

        if (!notification.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOTIFY001);
        }

        if (Boolean.FALSE.equals(notification.getIsRead())) {
            notification.setIsRead(true);
            notification.setReadAt(Instant.now());
            notificationRepository.save(notification);
            log.info("User {} marked notification {} as read", userId, notificationId);
        }
    }

    /**
     * Mark all of the current user's unread notifications as read.
     *
     * @return the number of notifications that were updated
     */
    @Transactional
    public int markAllAsRead() {
        UUID userId = getCurrentUserId();
        int count = notificationRepository.markAllAsRead(userId, Instant.now());
        log.info("User {} marked {} notifications as read (batch)", userId, count);
        return count;
    }

    /**
     * Get the unread notification count for the current user.
     *
     * @return the number of unread notifications
     */
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        UUID userId = getCurrentUserId();
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // ---- helpers ----

    private UUID getCurrentUserId() {
        String userIdStr = UserContext.getUserId();
        if (userIdStr == null) {
            throw new BusinessException(ErrorCode.AUTH001);
        }
        return UUID.fromString(userIdStr);
    }
}
