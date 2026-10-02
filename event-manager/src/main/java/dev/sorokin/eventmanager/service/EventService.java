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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        LocationEntity location = locationRepository.findById(eventCreateRequestDto.locationId()).orElseThrow(()->
                new NoSuchFoundException("Локация с id=%s не найдена".formatted(eventCreateRequestDto.locationId())));
        if(eventCreateRequestDto.maxPlaces()>location.getCapacity())
            throw new IllegalArgumentException("Количество мест у Event (%s) не может быть больше чем у локации (%s)"
                    .formatted(eventCreateRequestDto.maxPlaces(), location.getCapacity()));
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
        if (eventEntity.getEventStatus()!=EventStatus.WAIT_START)
            throw new IllegalArgumentException("Можно удалить только те события, которые не начались");
        eventEntity.setEventStatus(EventStatus.CANCELLED);
        eventRepository.save(eventEntity);
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
        if(eventUpdateDto.maxPlaces()>locationEntity.getCapacity())
            throw new IllegalArgumentException("Количество мест у Event (%s) не может быть больше чем у локации (%s)"
                    .formatted(eventUpdateDto.maxPlaces(), locationEntity.getCapacity()));
        if (eventUpdateDto.maxPlaces()<eventEntity.getOccupiedPlaces())
            throw new IllegalArgumentException("Количество максимальных мест не может быть меньше уже забронированных");
        eventEntity.setMaxPlaces(eventUpdateDto.maxPlaces());
        eventEntity.setLocation(locationEntity);
        eventEntity.setName(eventUpdateDto.name());
        eventEntity.setCost(eventUpdateDto.cost());
        eventEntity.setDuration(eventUpdateDto.duration());
        eventEntity.setDateStart(LocalDateTime.parse(eventUpdateDto.date()));
        EventEntity eventUpdated = eventRepository.save(eventEntity);
        return eventEntityMapper.toDomain(eventUpdated);
    }

    public List<Event> findAllEventsCurrentUser(UserEntity user) {
        List<EventEntity> eventEntities = eventRepository.findEventEntitiesByUser(user);
        return eventEntities.stream().map(eventEntityMapper::toDomain).toList();
    }

    public List<Event> search(EventSearchRequestDto eventSearchRequestDto) {
        Specification<EventEntity> specification = Specification.where(null);


        if (eventSearchRequestDto.name()!=null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("name"), eventSearchRequestDto.name()));
        }

        if (eventSearchRequestDto.durationMin()!=null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), eventSearchRequestDto.durationMin()));
        }

        if (eventSearchRequestDto.durationMax()!=null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("duration"), eventSearchRequestDto.durationMax()));
        }

        if (eventSearchRequestDto.costMin()!=null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("cost"), eventSearchRequestDto.costMin()));
        }

        if (eventSearchRequestDto.costMax()!=null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("cost"), eventSearchRequestDto.costMax()));
        }

        if (eventSearchRequestDto.dateStartBefore()!=null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("dateStart"), LocalDateTime.parse(eventSearchRequestDto.dateStartBefore())));
        }

        if (eventSearchRequestDto.dateStartAfter()!=null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("dateStart"), LocalDateTime.parse(eventSearchRequestDto.dateStartAfter())));
        }

        if (eventSearchRequestDto.eventStatus()!=null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("eventStatus"), EventStatus.valueOf(eventSearchRequestDto.eventStatus())));
        }

        if (eventSearchRequestDto.locationId()!=null) {
            LocationEntity locationFound = locationRepository.findById(eventSearchRequestDto.locationId()).orElseThrow(()->
                    new NoSuchFoundException("Локация с id=%s не найдена".formatted(eventSearchRequestDto.locationId())));
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("location"), locationFound));
        }

        if (eventSearchRequestDto.placesMin()!=null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("maxPlaces"), eventSearchRequestDto.placesMin()));
        }

        if (eventSearchRequestDto.placesMax()!=null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("maxPlaces"), eventSearchRequestDto.placesMax()));
        }

        List<EventEntity> search = eventRepository.findAll(specification);


        return search.stream()
                .map(eventEntityMapper::toDomain)
                .toList();
    }
}
