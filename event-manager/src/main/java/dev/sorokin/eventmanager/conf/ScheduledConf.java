package dev.sorokin.eventmanager.conf;

import dev.sorokin.eventmanager.dto.EventSearchRequestDto;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.EventStatus;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.service.EventService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduledConf {
    private final EventRepository eventRepository;

    public ScheduledConf(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void scheduledChangeStatusForEvents(){
        log.info("Scheduled for change eventStatus start");

        int countChangeStatusForStarted = eventRepository.updateEventStatusByDateStart(
                Timestamp.from(OffsetDateTime.now().toInstant())
        );
        log.info("Changed status for STARTED count = {}", countChangeStatusForStarted);
        int countChangeStatusForFinished = eventRepository.updateEventStatusByDateEnd(
                Timestamp.from(OffsetDateTime.now().toInstant())
        );
        log.info("Changed status for FINISHED count = {}", countChangeStatusForFinished);
        log.info("Scheduled for change eventStatus end");
    }


}
