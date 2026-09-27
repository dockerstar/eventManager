package dev.sorokin.eventmanager.model;


import java.util.List;

public record Location (
        Long id,
        String name,
        String address,
        Integer capacity,
        String description,
        List<Event> eventList
) {
}
