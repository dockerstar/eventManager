package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.dto.EventDtoMapper;
import dev.sorokin.eventmanager.dto.EventResponseDto;
import dev.sorokin.eventmanager.entity.EventEntity;
import dev.sorokin.eventmanager.entity.UserEntity;
import dev.sorokin.eventmanager.entity.UserRegisterEventEntity;
import dev.sorokin.eventmanager.security.jwt.AuthenticateService;
import dev.sorokin.eventmanager.service.RegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/registrations")
public class RegisterController {
    private final RegisterService registerService;
    private final AuthenticateService authenticateService;
    private final EventDtoMapper eventDtoMapper;

    public RegisterController(RegisterService registerService, AuthenticateService authenticateService, EventDtoMapper eventDtoMapper) {
        this.registerService = registerService;
        this.authenticateService = authenticateService;
        this.eventDtoMapper = eventDtoMapper;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> register(
            @PathVariable Long id
    ) {
        UserEntity user = authenticateService.getAuthenticateUser();
        registerService.registerOnEvent(user, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable Long id
    ) {
        UserEntity user = authenticateService.getAuthenticateUser();
        registerService.cancelRegister(user, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDto>> findAllMy() {
        UserEntity user = authenticateService.getAuthenticateUser();
        return ResponseEntity.ok().body(registerService.findAllByUser(user).stream()
                .map(eventDtoMapper::toResponseDto)
                .toList());
    }
}
