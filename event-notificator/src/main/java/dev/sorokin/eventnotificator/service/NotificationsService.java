package dev.sorokin.eventnotificator.service;

import dev.sorokin.eventnotificator.conf.RedisCounterUnreadNotif;
import dev.sorokin.eventnotificator.entity.NotificationsEntity;
import dev.sorokin.eventnotificator.entity.NotificationsEntityMapper;
import dev.sorokin.eventnotificator.model.Notifications;
import dev.sorokin.eventnotificator.repository.NotifiationsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NotificationsService {
    private final NotifiationsRepository notifiationsRepository;
    private final NotificationsEntityMapper notificationsEntityMapper;
    private final RedisCounterUnreadNotif redisCounterUnreadNotif;

    public NotificationsService(NotifiationsRepository repository, NotificationsEntityMapper notificationsEntityMapper, RedisCounterUnreadNotif redisCounterUnreadNotif) {
        this.notifiationsRepository = repository;
        this.notificationsEntityMapper = notificationsEntityMapper;
        this.redisCounterUnreadNotif = redisCounterUnreadNotif;
    }

    @Transactional(readOnly = true)
    public List<Notifications> findUnReadNotifications(Long userId) {
        List<NotificationsEntity> notificationsEntities = notifiationsRepository.findNotificationsEntitiesByUserIdAndIsRead(
                userId,
                false
        );


        return notificationsEntities.stream()
                .map(notificationsEntityMapper::toDomain)
                .toList();
    }

    @Transactional
    public List<Notifications> readNotificationsForUser(Long userId) {
        List<NotificationsEntity> notificationsEntityList = notifiationsRepository.findNotificationsEntitiesByUserIdAndIsRead(userId, false);
        notificationsEntityList.forEach(notificationsEntity -> {
            notificationsEntity.setRead(true);
            notificationsEntity.setReadAt(LocalDateTime.now());
        });
        List<NotificationsEntity> notificationsEntityListSaved = notifiationsRepository.saveAll(notificationsEntityList);

        Long countCurrentUnreadNotif = (long) notifiationsRepository.findNotificationsEntitiesByUserIdAndIsRead(userId, false).size();

        redisCounterUnreadNotif.updateUnreadNotifForUser(userId, countCurrentUnreadNotif);


        return notificationsEntityListSaved.stream()
                .map(notificationsEntityMapper::toDomain)
                .toList();
    }

}
