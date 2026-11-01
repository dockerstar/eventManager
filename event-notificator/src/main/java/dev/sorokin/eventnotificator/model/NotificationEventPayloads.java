package dev.sorokin.eventnotificator.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationEventPayloads(
        Long payloadId,
        UUID messageId,
        String eventType,
        Long eventId,
        LocalDateTime occurredAt,
        Long changedById,
        Long ownerId,
        String payload
) {
}
