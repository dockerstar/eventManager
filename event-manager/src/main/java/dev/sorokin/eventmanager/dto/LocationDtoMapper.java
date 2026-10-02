package dev.sorokin.eventmanager.dto;

import dev.sorokin.eventmanager.model.Location;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocationDtoMapper {
    private final EventDtoMapper eventDtoMapper;

    public LocationDtoMapper(EventDtoMapper eventDtoMapper) {
        this.eventDtoMapper = eventDtoMapper;
    }

    public Location toDomain(LocationDto locationDto) {
        return new Location(
                null,
                locationDto.name(),
                locationDto.address(),
                locationDto.capacity(),
                locationDto.description(),
                List.of()
        );
    }

    public LocationDto toDto(Location location) {
        return new LocationDto(
                location.name(),
                location.address(),
                location.capacity(),
                location.description(),
                location.eventList().stream()
                        .map(eventDtoMapper::toDto)
                        .toList()
        );
    }
}
