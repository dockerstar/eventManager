package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.dto.EventCreateRequestDto;
import dev.sorokin.eventmanager.dto.EventDtoMapper;
import dev.sorokin.eventmanager.dto.EventResponseDto;
import dev.sorokin.eventmanager.dto.EventSearchRequestDto;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.security.jwt.AuthenticateService;
import dev.sorokin.eventmanager.service.EventService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Slf4j
public class EventController {
    private final EventService eventService;
    private final EventDtoMapper eventDtoMapper;
    private final AuthenticateService authenticateService;

    public EventController(EventService eventService, EventDtoMapper eventDtoMapper, AuthenticateService authenticateService) {
        this.eventService = eventService;
        this.eventDtoMapper = eventDtoMapper;
        this.authenticateService = authenticateService;
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> create(
            @RequestBody @Valid EventCreateRequestDto eventCreateRequestDto
    ) {
        UserEntity user = authenticateService.getAuthenticateUser();
        EventCreateRequestDto eventCreateRequestDtoUser = new EventCreateRequestDto(
                eventCreateRequestDto.date(),
                eventCreateRequestDto.duration(),
                eventCreateRequestDto.cost(),
                eventCreateRequestDto.maxPlaces(),
                eventCreateRequestDto.locationId(),
                eventCreateRequestDto.name(),
                user.getId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                eventDtoMapper.toResponseDto(eventService.save(eventCreateRequestDtoUser))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventDtoMapper.toResponseDto(eventService.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        UserEntity userContext = authenticateService.getAuthenticateUser();
        eventService.delete(id, userContext);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid EventCreateRequestDto eventUpdateDto
    ) {
        UserEntity userContext = authenticateService.getAuthenticateUser();
        return ResponseEntity.ok().body(
                eventDtoMapper.toResponseDto(eventService.update(id, eventUpdateDto, userContext))
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDto>> findAllEventForUser() {
        UserEntity userContext = authenticateService.getAuthenticateUser();
        return ResponseEntity.ok().body(
                eventService.findAllEventsCurrentUser(userContext).stream()
                        .map(eventDtoMapper::toResponseDto)
                        .toList()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventResponseDto>> search(
            @RequestBody EventSearchRequestDto eventSearchRequestDto
            ) {
        return ResponseEntity.ok().body(
                eventService.search(eventSearchRequestDto).stream()
                        .map(eventDtoMapper::toResponseDto)
                        .toList()
        );
    }
}
