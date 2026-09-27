package dev.sorokin.eventmanager.dto;

public record EventResponseDto(
         Long id,
         String name,
         Long ownerId,
         Integer maxPlaces,
         Integer occupiedPlaces,
         String date,
         Integer cost,
         Integer duration,
         Long locationId,
         String status
) {
}
