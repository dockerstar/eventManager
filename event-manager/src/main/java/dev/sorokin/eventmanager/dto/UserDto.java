package dev.sorokin.eventmanager.dto;

import dev.sorokin.eventmanager.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDto(
        String login,
        Integer age,
        UserRole role
) {
}
