package dev.sorokin.eventmanager.model;

import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.UserRole;

import java.util.List;

public record User(
        Long id,
        String login,
        String passwordHash,
        Integer age,
        UserRole role,
        List<Event> eventsList
) {
}
