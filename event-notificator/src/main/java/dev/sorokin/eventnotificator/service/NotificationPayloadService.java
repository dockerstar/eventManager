package dev.sorokin.eventnotificator.service;

import dev.sorokin.eventcommon.kafka.EventPayload;
import dev.sorokin.eventnotificator.entity.NotificationEventPayloadsEntity;
import dev.sorokin.eventnotificator.entity.NotificationPayloadsEntityMapper;
import dev.sorokin.eventnotificator.entity.NotificationsEntity;
import dev.sorokin.eventnotificator.model.NotificationEventPayloads;
import dev.sorokin.eventnotificator.repository.NotifiationsRepository;
import dev.sorokin.eventnotificator.repository.NotificationPayloadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationPayloadService {
    private final NotificationPayloadRepository repository;
    private final NotificationPayloadsEntityMapper mapper;
    private final NotifiationsRepository notifiationsRepository;

    public NotificationPayloadService(NotificationPayloadRepository repository, NotificationPayloadsEntityMapper mapper, NotifiationsRepository notifiationsRepository) {
        this.repository = repository;
        this.mapper = mapper;
        this.notifiationsRepository = notifiationsRepository;
    }

    @Transactional
    public NotificationEventPayloads save(EventPayload eventPayload) {
        NotificationEventPayloadsEntity notificationEventPayloads = repository
                .findNotificationEventPayloadsEntityInMessageIdAndOccurredAt(
                        eventPayload.getMessageId(),
                        eventPayload.getOccurredAt());
        if (notificationEventPayloads!=null) {
            throw new RuntimeException("Payloads been received: %s".formatted(notificationEventPayloads.getMessageId()));
        }

        NotificationEventPayloads eventPayloadsDomain = mapper.toDomain(eventPayload);
        NotificationEventPayloadsEntity eventPayloadsEntity = mapper.toEntity(eventPayloadsDomain);

        NotificationEventPayloadsEntity eventPayloadsEntityCreated =  repository.save(eventPayloadsEntity);

        List<Long> subscribers = eventPayload.getSubscribers();

        subscribers.forEach(subsribe -> {
            notifiationsRepository.save(
                    new NotificationsEntity(
                            null,
                            subsribe,
                            false,
                            LocalDateTime.now(),
                            null,
                            eventPayloadsEntityCreated
                            )
            );
        });

        return mapper.toDomain(eventPayloadsEntityCreated);
    }
}
