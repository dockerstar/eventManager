package dev.sorokin.eventcommon.kafka;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EventPayload {
    private UUID messageId;
    private EventType eventType;
    private Long eventId;
    private LocalDateTime occurredAt;
    private Long ownerId;
    private Long changedById;
    private List<Long> subscribers;
    private List<ChangesField> changes;

    public EventPayload(UUID messageId, EventType eventType, Long eventId, LocalDateTime occurredAt, Long ownerId, Long changedById, List<Long> subscribers, List<ChangesField> changes) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.ownerId = ownerId;
        this.changedById = changedById;
        this.subscribers = subscribers;
        this.changes = changes;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getChangedById() {
        return changedById;
    }

    public void setChangedById(Long changedById) {
        this.changedById = changedById;
    }

    public List<Long> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Long> subscribers) {
        this.subscribers = subscribers;
    }

    public List<ChangesField> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangesField> changes) {
        this.changes = changes;
    }

    @Override
    public String toString() {
        return "EventPayload{" +
                "messageId=" + messageId +
                ", eventType=" + eventType +
                ", eventId=" + eventId +
                ", occurredAt=" + occurredAt +
                ", ownerId=" + ownerId +
                ", changedById=" + changedById +
                ", subscribers=" + subscribers +
                ", changes=" + changes +
                '}';
    }
}
