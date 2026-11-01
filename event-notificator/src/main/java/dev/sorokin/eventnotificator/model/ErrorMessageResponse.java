package dev.sorokin.eventnotificator.model;

import java.time.LocalDateTime;

public record ErrorMessageResponse(
        String message,
        String detailedMessage,
        LocalDateTime localDateTime
) {
}
