package dev.sorokin.eventmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.util.List;

public record LocationDto(
        @NotBlank(message = "Поле name не должно быть пустым")
        String name,
        @NotBlank(message = "Поле address не должно быть пустым")
        String address,
        @NotNull(message = "Поле capacity не должно быть пустым")
        Integer capacity,
        String description,
        List<EventCreateRequestDto> eventList
) {
}
