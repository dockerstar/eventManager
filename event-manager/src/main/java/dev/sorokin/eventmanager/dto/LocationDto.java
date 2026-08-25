package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationDto(
        @NotBlank(message = "Поле name не должно быть пустым")
        String name,
        @NotBlank(message = "Поле address не должно быть пустым")
        String address,
        @NotNull(message = "Поле capacity не должно быть пустым")
        Integer capacity,
        String description
) {
}
