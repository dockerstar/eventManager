package dev.sorokin.eventmanager.controller;

import dev.sorokin.eventmanager.dto.LocationDto;
import dev.sorokin.eventmanager.dto.LocationDtoMapper;
import dev.sorokin.eventmanager.entity.LocationEntityMapper;
import dev.sorokin.eventmanager.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {
    private final LocationService locationService;
    private final LocationDtoMapper locationDtoMapper;

    public LocationController(LocationService locationService, LocationDtoMapper locationDtoMapper) {
        this.locationService = locationService;
        this.locationDtoMapper = locationDtoMapper;
    }

    @PostMapping
    public ResponseEntity<LocationDto> create(
            @RequestBody @Valid LocationDto locationDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                locationDtoMapper.toDto(locationService.save(locationDto))
        );
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> findAll() {
        return ResponseEntity.ok(
                locationService.findAll().stream()
                        .map(locationDtoMapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                locationDtoMapper.toDto(locationService.findById(id))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> update(
            @PathVariable Long id,
            @RequestBody @Valid LocationDto locationDto
    ) {
        return ResponseEntity.ok(
                locationDtoMapper.toDto(locationService.update(id, locationDto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
