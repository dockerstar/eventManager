package dev.sorokin.eventnotificator.service;

import dev.sorokin.eventcommon.kafka.EventPayload;
import dev.sorokin.eventnotificator.entity.NotificationsEntity;
import dev.sorokin.eventnotificator.entity.NotificationsEntityMapper;
import dev.sorokin.eventnotificator.model.Notifications;
import dev.sorokin.eventnotificator.repository.NotifiationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationsService {
    private final NotifiationsRepository notifiationsRepository;
    private final NotificationsEntityMapper notificationsEntityMapper;

    public NotificationsService(NotifiationsRepository repository, NotificationsEntityMapper notificationsEntityMapper) {
        this.notifiationsRepository = repository;
        this.notificationsEntityMapper = notificationsEntityMapper;
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
                        notificationsEntity.setReadAt(LocalDateTime.now());});
        List<NotificationsEntity> notificationsEntityListSaved = notifiationsRepository.saveAll(notificationsEntityList);

        return notificationsEntityListSaved.stream()
                .map(notificationsEntityMapper::toDomain)
                .toList();
    }

}
