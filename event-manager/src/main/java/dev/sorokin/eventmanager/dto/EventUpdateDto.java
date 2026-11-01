package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record EventUpdateDto(
        String date,

        @Min(30)
        Integer duration,

        @Min(1)
        Integer cost,

        @Min(1)
        Integer maxPlaces,

        Long locationId,

        String name,

        Long ownerId
) {
}
