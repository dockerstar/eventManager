package dev.sorokin.eventnotificator.entity;

import dev.sorokin.eventnotificator.model.Notifications;
import org.springframework.stereotype.Component;

@Component
public class NotificationsEntityMapper {
    private final NotificationPayloadsEntityMapper notificationPayloadsEntityMapper;

    public NotificationsEntityMapper(NotificationPayloadsEntityMapper notificationPayloadsEntityMapper) {
        this.notificationPayloadsEntityMapper = notificationPayloadsEntityMapper;
    }

    public Notifications toDomain(NotificationsEntity notifications) {
        return new Notifications(
                notifications.getId(),
                notifications.getUserId(),
                notifications.isRead(),
                notifications.getCreatedAt(),
                notifications.getReadAt(),
                notificationPayloadsEntityMapper.toDomain(notifications.getPayload())
        );
    }

    public NotificationsEntity toEntity(Notifications notifications) {
        return new NotificationsEntity(
                notifications.id(),
                notifications.userId(),
                notifications.isRead(),
                notifications.createdAt(),
                notifications.readAt(),
                notificationPayloadsEntityMapper.toEntity(notifications.payload())
        );
    }
}
