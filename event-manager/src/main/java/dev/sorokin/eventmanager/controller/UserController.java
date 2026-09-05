package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.dto.UserDto;
import dev.sorokin.eventmanager.dto.UserDtoMapper;
import dev.sorokin.eventmanager.model.SignUpUserRequest;
import dev.sorokin.eventmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    public UserController(UserService userService, UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }

    @PostMapping
    public ResponseEntity<UserDto> create(
            @RequestBody @Valid SignUpUserRequest signInUserRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userDtoMapper.toDto(userService.save(signInUserRequest)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userDtoMapper.toDto(userService.findById(id)));
    }
}
