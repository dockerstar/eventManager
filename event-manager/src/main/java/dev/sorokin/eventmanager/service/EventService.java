package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.dto.EventCreateRequestDto;
import dev.sorokin.eventmanager.dto.EventDtoMapper;
import dev.sorokin.eventmanager.dto.EventSearchRequestDto;
import dev.sorokin.eventmanager.entity.*;
import dev.sorokin.eventmanager.exception.NoPermissonToPerfom;
import dev.sorokin.eventmanager.exception.NoSuchFoundException;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.LocationRepository;
import dev.sorokin.eventmanager.security.jwt.AuthenticateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final EventEntityMapper eventEntityMapper;
    private final LocationRepository locationRepository;
    private final AuthenticateService authenticateService;
    public EventService(EventRepository eventRepository, EventDtoMapper eventDtoMapper, EventEntityMapper eventEntityMapper, LocationRepository locationRepository, AuthenticateService authenticateService) {
        this.eventRepository = eventRepository;
        this.eventDtoMapper = eventDtoMapper;
        this.eventEntityMapper = eventEntityMapper;
        this.locationRepository = locationRepository;
        this.authenticateService = authenticateService;
    }

    public Event save(EventCreateRequestDto eventCreateRequestDto) {
        if(!locationRepository.existsById(eventCreateRequestDto.locationId()))
                throw new NoSuchFoundException("Локация с id=%s не найдена".formatted(eventCreateRequestDto.locationId()));
        Event eventCreated = eventDtoMapper.toDomain(eventCreateRequestDto);
        log.info("info log created domain: event = {}", eventCreated);
        EventEntity eventEntity = eventRepository.save(eventEntityMapper.toEntity(eventCreated));
        return eventEntityMapper.toDomain(eventEntity);
    }

    public Event findById(Long id) {
        EventEntity eventEntity = eventRepository.findById(id).orElseThrow(
                ()-> new NoSuchFoundException("Событие с id=%s не найдено".formatted(id))
        );
        return eventEntityMapper.toDomain(eventEntity);
    }

    public void delete(Long id, UserEntity user) {
        EventEntity eventEntity = eventRepository.findById(id).orElseThrow(
                ()-> new NoSuchFoundException("Событие с id=%s не найдено".formatted(id))
        );
        if (!Objects.equals(eventEntity.getUser().getId(), user.getId()) && !Objects.equals(user.getRole(), UserRole.ADMIN))
            throw new NoPermissonToPerfom("У пользователя с id=%s нет прав на выполнения".formatted(user.getId()));
        eventRepository.delete(eventEntity);
    }

    public Event update(Long id, EventCreateRequestDto eventUpdateDto, UserEntity user) {
        EventEntity eventEntity = eventRepository.findById(id).orElseThrow(
                ()-> new NoSuchFoundException("Событие с id=%s не найдено".formatted(id))
        );
        if (!Objects.equals(eventEntity.getUser().getId(), user.getId()) && !Objects.equals(user.getRole(), UserRole.ADMIN))
            throw new NoPermissonToPerfom("У пользователя с id=%s нет прав на выполнения".formatted(user.getId()));
        LocationEntity locationEntity = locationRepository.findById(eventUpdateDto.locationId()).orElseThrow(
                ()-> new NoSuchFoundException("Локация с id=%s не найдена".formatted(eventUpdateDto.locationId()))
        );
        if (eventUpdateDto.maxPlaces()<eventEntity.getOccupiedPlaces())
            throw new IllegalArgumentException("Количество максимальных мест не может быть меньше уже забронированных");
        eventEntity.setMaxPlaces(eventUpdateDto.maxPlaces());
        eventEntity.setLocation(locationEntity);
        eventEntity.setName(eventUpdateDto.name());
        eventEntity.setCost(eventUpdateDto.cost());
        eventEntity.setDuration(eventUpdateDto.duration());
        eventEntity.setDateStart(OffsetDateTime.parse(eventUpdateDto.date()));
        EventEntity eventUpdated = eventRepository.save(eventEntity);
        return eventEntityMapper.toDomain(eventUpdated);
    }

    public List<Event> findAllEventsCurrentUser(UserEntity user) {
        List<EventEntity> eventEntities = eventRepository.findEventEntitiesByUser(user);
        return eventEntities.stream().map(eventEntityMapper::toDomain).toList();
    }

    public List<Event> search(EventSearchRequestDto eventSearchRequestDto) {
        List<EventEntity> eventEntities =
                eventRepository.search(
                        eventSearchRequestDto.name(),
                        eventSearchRequestDto.placesMin(),
                        eventSearchRequestDto.placesMax(),
                        OffsetDateTime.parse(eventSearchRequestDto.dateStartAfter()),
                        OffsetDateTime.parse(eventSearchRequestDto.dateStartBefore()),
                        eventSearchRequestDto.costMin(),
                        eventSearchRequestDto.costMax(),
                        eventSearchRequestDto.durationMin(),
                        eventSearchRequestDto.durationMax(),
                        locationRepository.findById(eventSearchRequestDto.locationId()).get(),
                        EventStatus.valueOf(eventSearchRequestDto.eventStatus())
                );
        return eventEntities.stream()
                .map(eventEntityMapper::toDomain)
                .toList();
    }
}
