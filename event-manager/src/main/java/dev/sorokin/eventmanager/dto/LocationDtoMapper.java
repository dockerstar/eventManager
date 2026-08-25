package dev.sorokin.eventmanager.dto;

import dev.sorokin.eventmanager.model.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationDtoMapper {

    public Location toDomain(LocationDto locationDto) {
        return new Location(
                null,
                locationDto.name(),
                locationDto.address(),
                locationDto.capacity(),
                locationDto.description()
        );
    }

    public LocationDto toDto(Location location) {
        return new LocationDto(
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }
}
