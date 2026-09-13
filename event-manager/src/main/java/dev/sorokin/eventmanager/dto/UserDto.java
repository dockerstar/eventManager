package dev.sorokin.eventmanager.dto;

import dev.sorokin.eventmanager.entity.UserRole;

public record UserDto(
        String login,
        Integer age,
        UserRole role
) {
}
