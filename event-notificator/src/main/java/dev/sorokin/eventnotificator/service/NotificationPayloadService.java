package dev.sorokin.eventnotificator.service;

import dev.sorokin.eventcommon.kafka.EventPayload;
import dev.sorokin.eventnotificator.conf.RedisCounterUnreadNotif;
import dev.sorokin.eventnotificator.entity.NotificationEventPayloadsEntity;
import dev.sorokin.eventnotificator.entity.NotificationPayloadsEntityMapper;
import dev.sorokin.eventnotificator.entity.NotificationsEntity;
import dev.sorokin.eventnotificator.model.NotificationEventPayloads;
import dev.sorokin.eventnotificator.repository.NotifiationsRepository;
import dev.sorokin.eventnotificator.repository.NotificationPayloadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationPayloadService {
    private final NotificationPayloadRepository repository;
    private final NotificationPayloadsEntityMapper mapper;
    private final NotifiationsRepository notifiationsRepository;
    private final RedisCounterUnreadNotif redisCounterUnreadNotif;

    public NotificationPayloadService(NotificationPayloadRepository repository, NotificationPayloadsEntityMapper mapper, NotifiationsRepository notifiationsRepository, RedisCounterUnreadNotif redisCounterUnreadNotif) {
        this.repository = repository;
        this.mapper = mapper;
        this.notifiationsRepository = notifiationsRepository;
        this.redisCounterUnreadNotif = redisCounterUnreadNotif;
    }


    @Transactional
    public NotificationEventPayloads save(EventPayload eventPayload) {
        NotificationEventPayloadsEntity notificationEventPayloads = repository
                .findNotificationEventPayloadsEntityInMessageIdAndOccurredAt(
                        eventPayload.getMessageId(),
                        eventPayload.getOccurredAt());
        if (notificationEventPayloads != null) {
            throw new IllegalArgumentException("Payloads been received: %s".formatted(notificationEventPayloads.getMessageId()));
        }

        NotificationEventPayloads eventPayloadsDomain = mapper.toDomain(eventPayload);
        NotificationEventPayloadsEntity eventPayloadsEntity = mapper.toEntity(eventPayloadsDomain);

        NotificationEventPayloadsEntity eventPayloadsEntityCreated = repository.save(eventPayloadsEntity);

        List<Long> subscribers = eventPayload.getSubscribers();


        subscribers.forEach(subsсribe -> {
            notifiationsRepository.save(
                    new NotificationsEntity(
                            null,
                            subsсribe,
                            false,
                            LocalDateTime.now(),
                            null,
                            eventPayloadsEntityCreated
                    )
            );
            redisCounterUnreadNotif.addUnreadNotifForUser(subsсribe);
        });


        return mapper.toDomain(eventPayloadsEntityCreated);
    }
}
