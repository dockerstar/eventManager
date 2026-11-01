package dev.sorokin.eventnotificator.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notifications")
public class NotificationsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payload_id", referencedColumnName = "id", nullable = false)
    private NotificationEventPayloadsEntity payload;

    public NotificationsEntity() {
    }

    public NotificationsEntity(Long id, Long userId, boolean isRead, LocalDateTime createdAt, LocalDateTime readAt, NotificationEventPayloadsEntity payload) {
        this.id = id;
        this.userId = userId;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.payload = payload;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public NotificationEventPayloadsEntity getPayload() {
        return payload;
    }

    public void setPayload(NotificationEventPayloadsEntity payload) {
        this.payload = payload;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
