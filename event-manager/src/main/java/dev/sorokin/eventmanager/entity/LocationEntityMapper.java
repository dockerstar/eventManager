package dev.sorokin.eventmanager.entity;

import dev.sorokin.eventmanager.model.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationEntityMapper {
    private final EventEntityMapper eventEntityMapper;

    public LocationEntityMapper(EventEntityMapper eventEntityMapper) {
        this.eventEntityMapper = eventEntityMapper;
    }

    public LocationEntity toEntity(Location location) {
        return new LocationEntity(
                location.id(),
                location.name(),
                location.address(),
                location.capacity(),
                location.description(),
                location.eventList().stream()
                        .map(eventEntityMapper::toEntity)
                        .toList()
        );
    }

    public Location toDomain(LocationEntity locationEntity) {
        return new Location(
                locationEntity.getId(),
                locationEntity.getName(),
                locationEntity.getAddress(),
                locationEntity.getCapacity(),
                locationEntity.getDescription(),
                locationEntity.getEventEntities().stream()
                        .map(eventEntityMapper::toDomain)
                        .toList()
        );
    }
}
