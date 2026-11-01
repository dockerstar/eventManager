package dev.sorokin.eventnotificator.repository;

import dev.sorokin.eventnotificator.entity.NotificationEventPayloadsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface NotificationPayloadRepository extends JpaRepository<NotificationEventPayloadsEntity, Long> {
    @Query("""
        select n from NotificationEventPayloadsEntity n 
            where n.messageId = :messageId
                and n.occurredAt = :occurredAt
    """)
    NotificationEventPayloadsEntity findNotificationEventPayloadsEntityInMessageIdAndOccurredAt(
            @Param("messageId") UUID messageId,
            @Param("occurredAt")LocalDateTime occurredAt
            );
}
