package dev.sorokin.eventmanager.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthUserRequest(
        @NotBlank(message = "Поле login не должно быть пустым")
        String login,
        @NotBlank(message = "Поле password не должно быть пустым")
        String password
) {
}
