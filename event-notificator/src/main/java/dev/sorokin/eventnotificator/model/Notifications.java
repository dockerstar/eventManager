package dev.sorokin.eventnotificator.model;

import java.time.LocalDateTime;

public record Notifications(
        Long id,
        Long userId,
        boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime readAt,
        NotificationEventPayloads payload
) {
}
