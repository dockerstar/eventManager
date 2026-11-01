package dev.sorokin.eventnotificator.conf;

import dev.sorokin.eventnotificator.repository.NotifiationsRepository;
import jakarta.persistence.Enumerated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduledConf {
    private final NotifiationsRepository notifiationsRepository;

    public ScheduledConf(NotifiationsRepository notifiationsRepository) {
        this.notifiationsRepository = notifiationsRepository;
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void scheduledDeleteOldNotifications() {
        notifiationsRepository.deleteNotificationsEntitiesByReadAtBefore(Timestamp.from(Instant.now()));
    }
}
