package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record EventCreateRequestDto(
        @NotBlank
        String date,

        @NotNull
        @Min(30)
        Integer duration,

        @NotNull
        @Min(1)
        Integer cost,

        @NotNull
        @Min(1)
        Integer maxPlaces,

        @NotNull
        Long locationId,

        @NotBlank
        String name,

        Long ownerId
) {
}
