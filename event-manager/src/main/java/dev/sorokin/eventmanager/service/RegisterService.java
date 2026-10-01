package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.dto.EventDtoMapper;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.EventEntityMapper;
import dev.sorokin.eventmanager.entity.EventStatus;
import dev.sorokin.eventmanager.exception.NoSuchFoundException;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.repository.EventRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class RegisterService {
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final EventEntityMapper eventEntityMapper;

    public RegisterService(EventRepository eventRepository, EventDtoMapper eventDtoMapper, EventEntityMapper eventEntityMapper) {
        this.eventRepository = eventRepository;
        this.eventDtoMapper = eventDtoMapper;
        this.eventEntityMapper = eventEntityMapper;
    }

    @Transactional
    public void registerOnEvent(Long userId, Long eventId) {
        EventEntity eventFound = eventRepository.findById(eventId).orElseThrow(()->
                new NoSuchFoundException("События с id=%s не найдено".formatted(eventId)));
        if (!eventFound.getEventStatus().equals(EventStatus.WAIT_START))
            throw new IllegalArgumentException("Event имеет статус %s".formatted(eventFound.getEventStatus()));
        if (Objects.equals(eventFound.getOccupiedPlaces(), eventFound.getMaxPlaces()))
            throw new IllegalArgumentException("У Event все места заняты");
        eventFound.setOccupiedPlaces(eventFound.getOccupiedPlaces()+1);
        log.info("User id=%s register for event id=%s".formatted(userId, eventId));
    }

    public void cancelRegister(Long userId, Long eventId) {
        EventEntity eventFound = eventRepository.findById(eventId).orElseThrow(()->
                new NoSuchFoundException("События с id=%s не найдено".formatted(eventId)));
        if (eventFound.getEventStatus().equals(EventStatus.WAIT_START))
            throw new IllegalArgumentException("Event имеет статус %s, отменить регистрацию не получится"
                    .formatted(eventFound.getEventStatus()));
        eventFound.setOccupiedPlaces(eventFound.getOccupiedPlaces()-1);
        log.info("User id=%s cancel register for event id=%s".formatted(userId, eventId));
    }
}
