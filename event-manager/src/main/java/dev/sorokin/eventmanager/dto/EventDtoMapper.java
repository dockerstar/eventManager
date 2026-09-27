package dev.sorokin.eventmanager.dto;

import dev.sorokin.eventmanager.entity.EventStatus;
import dev.sorokin.eventmanager.exception.NoSuchFoundException;
import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.repository.LocationRepository;
import dev.sorokin.eventmanager.security.jwt.AuthenticateService;
import org.springframework.stereotype.Component;

@Component
public class EventDtoMapper {
    private final LocationRepository locationRepository;
    public EventDtoMapper(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Event toDomain(EventCreateRequestDto eventCreateRequestDto) {
        return new Event(
                null,
                eventCreateRequestDto.name(),
                eventCreateRequestDto.maxPlaces(),
                eventCreateRequestDto.date(),
                eventCreateRequestDto.cost(),
                eventCreateRequestDto.duration(),
                eventCreateRequestDto.locationId(),
                EventStatus.WAIT_START,
                eventCreateRequestDto.ownerId(),
                0
        );
    }

    public EventCreateRequestDto toDto(Event event) {
        return new EventCreateRequestDto(
                event.dateTime(),
                event.duration(),
                event.cost(),
                event.maxPlaces(),
                event.locationId(),
                event.name(),
                event.ownerId()
        );
    }

    public EventResponseDto toResponseDto(Event event) {
        return new EventResponseDto(
                event.id(),
                event.name(),
                event.ownerId(),
                event.maxPlaces(),
                event.occupiedPlaces(),
                event.dateTime(),
                event.cost(),
                event.duration(),
                event.locationId(),
                event.status().toString()
        );
    }
}
