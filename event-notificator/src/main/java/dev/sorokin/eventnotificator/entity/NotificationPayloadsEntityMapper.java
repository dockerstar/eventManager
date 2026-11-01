package dev.sorokin.eventnotificator.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventcommon.kafka.EventPayload;
import dev.sorokin.eventnotificator.model.NotificationEventPayloads;
import org.springframework.stereotype.Component;

@Component
public class NotificationPayloadsEntityMapper {

    public NotificationEventPayloads toDomain(
            NotificationEventPayloadsEntity notificationEventPayloadsEntity
    ) {
        return new NotificationEventPayloads(
                notificationEventPayloadsEntity.getId(),
                notificationEventPayloadsEntity.getMessageId(),
                notificationEventPayloadsEntity.getEventType(),
                notificationEventPayloadsEntity.getEventId(),
                notificationEventPayloadsEntity.getOccurredAt(),
                notificationEventPayloadsEntity.getChangedById(),
                notificationEventPayloadsEntity.getOwnerId(),
                notificationEventPayloadsEntity.getPayloadJson()
        );
    }

    public NotificationEventPayloadsEntity toEntity(
            NotificationEventPayloads notificationEventPayloads
    ) {
        return new NotificationEventPayloadsEntity(
                notificationEventPayloads.payloadId(),
                notificationEventPayloads.messageId(),
                notificationEventPayloads.eventType(),
                notificationEventPayloads.eventId(),
                notificationEventPayloads.occurredAt(),
                notificationEventPayloads.ownerId(),
                notificationEventPayloads.changedById(),
                notificationEventPayloads.payload()
        );
    }

    public NotificationEventPayloads toDomain(
            EventPayload eventPayload
    ) {
        return new NotificationEventPayloads(
                null,
                eventPayload.getMessageId(),
                eventPayload.getEventType().toString(),
                eventPayload.getEventId(),
                eventPayload.getOccurredAt(),
                eventPayload.getChangedById(),
                eventPayload.getOwnerId(),
                new Payload(
                        eventPayload.getEventId(),
                        eventPayload.getChangedById(),
                        eventPayload.getChanges()
                ).toString()
        );
    }
}
