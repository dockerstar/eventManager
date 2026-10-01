package dev.sorokin.eventmanager.entity;

import dev.sorokin.eventmanager.model.Event;
import dev.sorokin.eventmanager.repository.LocationRepository;
import dev.sorokin.eventmanager.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Component
public class EventEntityMapper {
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public EventEntityMapper(LocationRepository locationRepository, UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    public EventEntity toEntity(Event event) {
        return new EventEntity(
                event.id(),
                event.name(),
                OffsetDateTime.parse(event.dateTime()),
                event.duration(),
                event.maxPlaces(),
                event.cost(),
                event.occupiedPlaces(),
                locationRepository.findById(Long.valueOf(event.locationId())).get(),
                event.status(),
                userRepository.findById(event.ownerId()).get()
        );
    }

    public Event toDomain(EventEntity eventEntity) {
        return new Event(
                eventEntity.getId(),
                eventEntity.getName(),
                eventEntity.getMaxPlaces(),
                String.valueOf(eventEntity.getDateStart()),
                eventEntity.getCost(),
                eventEntity.getDuration(),
                eventEntity.getLocation().getId(),
                eventEntity.getEventStatus(),
                eventEntity.getUser().getId(),
                eventEntity.getOccupiedPlaces()
        );
    }
}
