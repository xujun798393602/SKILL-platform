package com.skill.platform.notify.model.dto;

import com.skill.platform.notify.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for notification responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID id;
    private String type;
    private String title;
    private String content;
    private String data;
    private Boolean isRead;
    private Instant readAt;
    private Instant createdAt;

    /**
     * Convert a {@link Notification} entity to a {@link NotificationResponse}.
     *
     * @param notification the notification entity
     * @return the response DTO
     */
    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .data(notification.getData())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
