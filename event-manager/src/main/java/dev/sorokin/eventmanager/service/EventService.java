package dev.sorokin.eventmanager.service;

import dev.sorokin.eventcommon.kafka.ChangesField;
import dev.sorokin.eventcommon.kafka.EventPayload;
import dev.sorokin.eventcommon.kafka.EventType;
import dev.sorokin.eventmanager.dto.EventCreateRequestDto;
import dev.sorokin.eventmanager.dto.EventDtoMapper;
import dev.sorokin.eventmanager.dto.EventSearchRequestDto;
import dev.sorokin.eventmanager.dto.EventUpdateDto;
import dev.sorokin.eventmanager.entity.*;
import dev.sorokin.eventmanager.exception.NoPermissonToPerfom;
import dev.sorokin.eventmanager.exception.NoSuchFoundException;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.repository.EventRepository;
import dev.sorokin.eventmanager.repository.LocationRepository;
import dev.sorokin.eventmanager.repository.UserRegisterEventRepository;
import dev.sorokin.eventmanager.security.jwt.AuthenticateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final EventEntityMapper eventEntityMapper;
    private final LocationRepository locationRepository;
    private final AuthenticateService authenticateService;
    private final KafkaTemplate<String, EventPayload> kafkaTemplate;
    private final UserRegisterEventRepository register;

    public EventService(EventRepository eventRepository, EventDtoMapper eventDtoMapper, EventEntityMapper eventEntityMapper, LocationRepository locationRepository, AuthenticateService authenticateService, KafkaTemplate<String, EventPayload> kafkaTemplate, UserRegisterEventRepository register) {
        this.eventRepository = eventRepository;
        this.eventDtoMapper = eventDtoMapper;
        this.eventEntityMapper = eventEntityMapper;
        this.locationRepository = locationRepository;
        this.authenticateService = authenticateService;
        this.kafkaTemplate = kafkaTemplate;
        this.register = register;
    }

    public Event save(EventCreateRequestDto eventCreateRequestDto) {
        LocationEntity location = locationRepository.findById(eventCreateRequestDto.locationId()).orElseThrow(() ->
                new NoSuchFoundException("Локация с id=%s не найдена".formatted(eventCreateRequestDto.locationId())));
        if (eventCreateRequestDto.maxPlaces() > location.getCapacity()) {
            throw new IllegalArgumentException("Количество мест у Event (%s) не может быть больше чем у локации (%s)"
                    .formatted(eventCreateRequestDto.maxPlaces(), location.getCapacity()));
        }
        Event eventCreated = eventDtoMapper.toDomain(eventCreateRequestDto);
        log.info("info log created domain: event = {}", eventCreated);
        EventEntity eventEntity = eventRepository.save(eventEntityMapper.toEntity(eventCreated));
        return eventEntityMapper.toDomain(eventEntity);
    }

    public Event findById(Long id) {
        EventEntity eventEntity = eventRepository.findById(id).orElseThrow(
                () -> new NoSuchFoundException("Событие с id=%s не найдено".formatted(id))
        );
        return eventEntityMapper.toDomain(eventEntity);
    }

    public void delete(Long id, UserEntity user) {
        EventEntity eventEntity = eventRepository.findById(id).orElseThrow(
                () -> new NoSuchFoundException("Событие с id=%s не найдено".formatted(id))
        );
        if (!Objects.equals(eventEntity.getUser().getId(), user.getId()) && !Objects.equals(user.getRole(), UserRole.ADMIN))
            throw new NoPermissonToPerfom("У пользователя с id=%s нет прав на выполнения".formatted(user.getId()));
        if (eventEntity.getEventStatus() != EventStatus.WAIT_START)
            throw new IllegalArgumentException("Можно удалить только те события, которые не начались");
        List<ChangesField> changesFields = new ArrayList<>();
        changesFields.add(
                new ChangesField(
                        "EventStatus",
                        eventEntity.getEventStatus().toString(),
                        EventStatus.CANCELLED.toString()
                )
        );
        eventEntity.setEventStatus(EventStatus.CANCELLED);

        EventPayload eventPayload = new EventPayload(
                UUID.randomUUID(),
                EventType.CANCELLED,
                eventEntity.getId(),
                LocalDateTime.now(),
                eventEntity.getUser().getId(),
                user.getId(),
                register.findAllSubscribersInEvent(eventEntity.getId()),
                changesFields
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

        eventRepository.save(eventEntity);
    }

    public Event update(Long id, EventUpdateDto eventUpdateDto, UserEntity user) {
        EventEntity eventEntity = eventRepository.findById(id).orElseThrow(
                () -> new NoSuchFoundException("Событие с id=%s не найдено".formatted(id))
        );
        if (eventEntity.getEventStatus().equals(EventStatus.FINISHED))
            throw new IllegalArgumentException("Событие уже завершилось");
        if (!Objects.equals(eventEntity.getUser().getId(), user.getId()) && !Objects.equals(user.getRole(), UserRole.ADMIN))
            throw new NoPermissonToPerfom("У пользователя с id=%s нет прав на выполнения".formatted(user.getId()));

        List<ChangesField> changesFields = new ArrayList<>();

    if (eventUpdateDto.locationId() != null) {
            LocationEntity locationEntity = locationRepository.findById(eventUpdateDto.locationId()).orElseThrow(
                    () -> new NoSuchFoundException("Локация с id=%s не найдена".formatted(eventUpdateDto.locationId()))
            );
            if (eventUpdateDto.maxPlaces() != null) {
                if (eventUpdateDto.maxPlaces() > locationEntity.getCapacity())
                    throw new IllegalArgumentException("Количество мест у Event (%s) не может быть больше чем у локации (%s)"
                            .formatted(eventUpdateDto.maxPlaces(), locationEntity.getCapacity()));
                if (eventUpdateDto.maxPlaces() < eventEntity.getOccupiedPlaces())
                    throw new IllegalArgumentException("Количество максимальных мест не может быть меньше уже забронированных");

                changesFields.add(new ChangesField(
                        "maxPlaces",
                        eventEntity.getMaxPlaces().toString(),
                        eventUpdateDto.maxPlaces().toString()
                ));
                eventEntity.setMaxPlaces(eventUpdateDto.maxPlaces());

            } else {
                if (eventEntity.getMaxPlaces() > locationEntity.getCapacity())
                    throw new IllegalArgumentException("Количество мест у Event (%s) не может быть больше чем у локации (%s)"
                            .formatted(eventEntity.getMaxPlaces(), locationEntity.getCapacity()));
                if (eventEntity.getMaxPlaces() < eventEntity.getOccupiedPlaces())
                    throw new IllegalArgumentException("Количество максимальных мест не может быть меньше уже забронированных");
            }

            changesFields.add(
                    new ChangesField(
                            "location",
                            eventEntity.getLocation().getId().toString(),
                            eventUpdateDto.locationId().toString()
                    )
            );
            eventEntity.setLocation(locationEntity);
        }
        if (eventUpdateDto.name()!=null) {
            changesFields.add(
                    new ChangesField(
                            "name",
                            eventEntity.getName(),
                            eventUpdateDto.name()
                    )
            );
            eventEntity.setName(eventUpdateDto.name());
        }
        if (eventUpdateDto.cost()!=null) {
            changesFields.add(
                    new ChangesField(
                            "cost",
                            eventEntity.getCost().toString(),
                            eventUpdateDto.cost().toString()
                    )
            );
            eventEntity.setCost(eventUpdateDto.cost());
        }
        if (eventUpdateDto.duration()!=null) {
            changesFields.add(
                    new ChangesField(
                            "duration",
                            eventEntity.getDuration().toString(),
                            eventUpdateDto.duration().toString()
                    )
            );
            eventEntity.setDuration(eventUpdateDto.duration());
        }
        if (eventUpdateDto.date()!=null) {
            changesFields.add(
                    new ChangesField(
                            "date",
                            eventEntity.getDateStart().toString(),
                            eventUpdateDto.date()
                    )
            );
            eventEntity.setDateStart(OffsetDateTime.parse(eventUpdateDto.date()).toLocalDateTime());
        }
            EventEntity eventUpdated = eventRepository.save(eventEntity);
        EventPayload eventPayload = new EventPayload(
                UUID.randomUUID(),
                EventType.UPDATED,
                eventUpdated.getId(),
                LocalDateTime.now(),
                eventUpdated.getUser().getId(),
                user.getId(),
                register.findAllSubscribersInEvent(eventEntity.getId()),
                changesFields
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
    return eventEntityMapper.toDomain(eventUpdated);
    }

    public List<Event> findAllEventsCurrentUser(UserEntity user) {
        List<EventEntity> eventEntities = eventRepository.findEventEntitiesByUser(user);
        return eventEntities.stream().map(eventEntityMapper::toDomain).toList();
    }

    public List<Event> search(EventSearchRequestDto eventSearchRequestDto) {
        Specification<EventEntity> specification = Specification.where(null);


        if (eventSearchRequestDto.name() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("name"), eventSearchRequestDto.name()));
        }

        if (eventSearchRequestDto.durationMin() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("duration"), eventSearchRequestDto.durationMin()));
        }

        if (eventSearchRequestDto.durationMax() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("duration"), eventSearchRequestDto.durationMax()));
        }

        if (eventSearchRequestDto.costMin() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("cost"), eventSearchRequestDto.costMin()));
        }

        if (eventSearchRequestDto.costMax() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("cost"), eventSearchRequestDto.costMax()));
        }

        if (eventSearchRequestDto.dateStartBefore() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("dateStart"), LocalDateTime.parse(eventSearchRequestDto.dateStartBefore())));
        }

        if (eventSearchRequestDto.dateStartAfter() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("dateStart"), LocalDateTime.parse(eventSearchRequestDto.dateStartAfter())));
        }

        if (eventSearchRequestDto.eventStatus() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("eventStatus"), EventStatus.valueOf(eventSearchRequestDto.eventStatus())));
        }

        if (eventSearchRequestDto.locationId() != null) {
            LocationEntity locationFound = locationRepository.findById(eventSearchRequestDto.locationId()).orElseThrow(() ->
                    new NoSuchFoundException("Локация с id=%s не найдена".formatted(eventSearchRequestDto.locationId())));
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("location"), locationFound));
        }

        if (eventSearchRequestDto.placesMin() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("maxPlaces"), eventSearchRequestDto.placesMin()));
        }

        if (eventSearchRequestDto.placesMax() != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("maxPlaces"), eventSearchRequestDto.placesMax()));
        }

        List<EventEntity> search = eventRepository.findAll(specification);


        return search.stream()
                .map(eventEntityMapper::toDomain)
                .toList();
    }
}
