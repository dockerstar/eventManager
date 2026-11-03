package dev.sorokin.eventmanager.conf;

import dev.sorokin.eventcommon.kafka.ChangesField;
import dev.sorokin.eventcommon.kafka.EventPayload;
import dev.sorokin.eventcommon.kafka.EventType;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.EventStatus;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.UserRegisterEventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@EnableCaching
@Configuration
@EnableScheduling
@Slf4j
public class ScheduledConf {
    private final EventRepository eventRepository;
    private final KafkaTemplate<String, EventPayload> kafkaTemplate;
    private final UserRegisterEventRepository register;
    private final StringRedisTemplate redisTemplate;

    public ScheduledConf(EventRepository eventRepository, KafkaTemplate<String, EventPayload> kafkaTemplate, UserRegisterEventRepository register, StringRedisTemplate redisTemplate) {
        this.eventRepository = eventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.register = register;
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void scheduledChangeStatusForEvents(){
        List<EventEntity> eventsChangeStatusForStarted = eventRepository.updateEventStatusByDateStart(
                EventStatus.WAIT_START.toString(),
                Timestamp.from(Instant.now())
        );

        if (!eventsChangeStatusForStarted.isEmpty()) {
            eventsChangeStatusForStarted.forEach(event -> {
                    event.setEventStatus(EventStatus.STARTED);
                    redisTemplate.opsForValue().getAndDelete("event:"+event.getId());
            });
            eventRepository.saveAll(eventsChangeStatusForStarted);
            eventsChangeStatusForStarted.forEach(event -> {
                EventPayload eventPayload = new EventPayload(
                        UUID.randomUUID(),
                        EventType.UPDATED,
                        event.getId(),
                        LocalDateTime.now(),
                        event.getUser().getId(),
                        null,
                        register.findAllSubscribersInEvent(event.getId()),
                        List.of(new ChangesField("status", EventStatus.WAIT_START.toString(), EventStatus.STARTED.toString()))
                );

                CompletableFuture<SendResult<String, EventPayload>> future = kafkaTemplate.send("event-changed-event-topic", eventPayload.getMessageId().toString(), eventPayload);
                future.whenComplete((result, exception) ->
                {
                    if (exception != null) {
                        log.error("Failed sent to message: {}", exception.getMessage());
                    } else {
                        log.info("Message sent successfully: {}", eventPayload);
                    }
                });
            });
        }

        List<EventEntity> eventsChangeStatusForFinished = eventRepository.updateEventStatusByDateEnd(
                EventStatus.STARTED.toString(),
                Timestamp.from(Instant.now())
        );

        if (!eventsChangeStatusForFinished.isEmpty()) {
            eventsChangeStatusForFinished.forEach(event -> {
                event.setEventStatus(EventStatus.FINISHED);
                redisTemplate.opsForValue().getAndDelete("event:"+event.getId());
            });
            eventRepository.saveAll(eventsChangeStatusForFinished);
            eventsChangeStatusForFinished.forEach(event -> {
                EventPayload eventPayload = new EventPayload(
                        UUID.randomUUID(),
                        EventType.UPDATED,
                        event.getId(),
                        LocalDateTime.now(),
                        event.getUser().getId(),
                        null,
                        register.findAllSubscribersInEvent(event.getId()),
                        List.of(new ChangesField("status", EventStatus.STARTED.toString(), EventStatus.FINISHED.toString()))
                );

                CompletableFuture<SendResult<String, EventPayload>> future = kafkaTemplate.send("event-changed-event-topic", eventPayload.getMessageId().toString(), eventPayload);
                future.whenComplete((result, exception) ->
                {
                    if (exception != null) {
                        log.error("Failed sent to message: {}", exception.getMessage());
                    } else {
                        log.info("Message sent successfully: {}", eventPayload);
                    }
                });
            });
        }
    }


}
