package dev.sorokin.eventmanager.conf;

import dev.sorokin.eventmanager.entity.EventStatus;
import dev.sorokin.eventmanager.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.*;
import java.time.Instant;

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
                EventStatus.STARTED.toString(),
                EventStatus.WAIT_START.toString(),
                Timestamp.from(Instant.now())
        );
        log.info("time test " +  Timestamp.from(Instant.now()));
        log.info("Changed status for STARTED count = {}", countChangeStatusForStarted);
        int countChangeStatusForFinished = eventRepository.updateEventStatusByDateEnd(
                EventStatus.FINISHED.toString(),
                EventStatus.STARTED.toString(),
                Timestamp.from(Instant.now())
        );
        log.info("Changed status for FINISHED count = {}", countChangeStatusForFinished);
        log.info("Scheduled for change eventStatus end");
    }


}
