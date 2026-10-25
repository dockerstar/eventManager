package dev.sorokin.eventnotificator.handler;

import dev.sorokin.eventcommon.kafka.EventPayload;
import dev.sorokin.eventnotificator.entity.NotificationEventPayloadsEntity;
import dev.sorokin.eventnotificator.model.NotificationEventPayloads;
import dev.sorokin.eventnotificator.service.NotificationPayloadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventChangedHandler {
    private final NotificationPayloadService notificationPayloadService;

    public EventChangedHandler(NotificationPayloadService notificationPayloadService) {
        this.notificationPayloadService = notificationPayloadService;
    }

    @KafkaListener(topics = "event-changed-event-topic", containerFactory = "containerFactory")
    public void handler(ConsumerRecord<String, EventPayload> consumerRecord) {
        log.info("Received event changed id: {}", consumerRecord.value().getMessageId());
        NotificationEventPayloads notificationEventPayloads = notificationPayloadService.save(consumerRecord.value());
        log.info("Saved event payloads fo DB: {}", notificationEventPayloads.messageId());
    }
}
