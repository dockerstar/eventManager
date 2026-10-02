package dev.sorokin.eventmanager.dto;

public record EventSearchRequestDto(
        String name,
        Integer placesMin,
        Integer placesMax,
        String dateStartAfter,
        String dateStartBefore,
        Integer costMin,
        Integer costMax,
        Integer durationMin,
        Integer durationMax,
        Long locationId,
        String eventStatus
) {
}
