package dev.sorokin.eventmanager.model;

import dev.sorokin.eventmanager.entity.EventStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Event(
        Long id,
        String name,
        Integer maxPlaces,
        String dateTime,
        Integer cost,
        Integer duration,
        Long locationId,
        EventStatus status,
        Long ownerId,
        Integer occupiedPlaces
) {
}
