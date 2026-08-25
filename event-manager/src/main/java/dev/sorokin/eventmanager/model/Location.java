package dev.sorokin.eventmanager.model;

import org.apache.kafka.common.protocol.types.Field;

public record Location (
        Long id,
        String name,
        String address,
        Integer capacity,
        String description
) {
}
