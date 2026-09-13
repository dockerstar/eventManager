package dev.sorokin.eventmanager.model;

import dev.sorokin.eventmanager.entity.UserRole;

public record User(
        Long id,
        String login,
        String passwordHash,
        Integer age,
        UserRole role
) {
}
