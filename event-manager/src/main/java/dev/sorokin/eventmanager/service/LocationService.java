package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.dto.LocationDto;
import dev.sorokin.eventmanager.dto.LocationDtoMapper;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.exception.ConflictException;
import dev.sorokin.eventmanager.exception.GlobalExceptionHandler;
import dev.sorokin.eventmanager.model.Location;
import dev.sorokin.eventmanager.entity.LocationEntityMapper;
import dev.sorokin.eventmanager.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationDtoMapper locationDtoMapper;
    private final LocationEntityMapper locationEntityMapper;
    private final Logger log = LoggerFactory.getLogger(LocationService.class);

    public LocationService(LocationRepository locationRepository, LocationDtoMapper locationDtoMapper, LocationEntityMapper locationEntityMapper) {
        this.locationRepository = locationRepository;
        this.locationDtoMapper = locationDtoMapper;
        this.locationEntityMapper = locationEntityMapper;
    }

    public Location save(LocationDto locationDto) {
        Location location = locationDtoMapper.toDomain(locationDto);
        LocationEntity locationEntity = locationRepository.save(locationEntityMapper.toEntity(location));

        log.info("Создана локация с id={}", locationEntity.getId());
        return location;
    }

    public List<Location> findAll() {
        return locationRepository.findAll().stream()
                .map(locationEntityMapper::toDomain)
                .toList();
    }

    public Location findById(Long id) {
        LocationEntity locationEntity = locationRepository.findById(id).orElseThrow(() ->
                new ConflictException("Лоакция с id = %s не найдена".formatted(id)));

        log.info("Был осущетвлен поиск лоакции с id={}", locationEntity.getId());
        return locationEntityMapper.toDomain(locationEntity);
    }

    public Location update(Long id, LocationDto locationDto) {
        LocationEntity locationEntity = locationRepository.findById(id).orElseThrow(() ->
                new ConflictException("Лоакция с id = %s не найдена".formatted(id)));
        Location updateLocation = new Location(
                locationEntity.getId(),
                locationDto.name(),
                locationDto.address(),
                locationEntity.getCapacity(),
                locationDto.description()
        );
        locationRepository.save(locationEntityMapper.toEntity(updateLocation));

        log.info("Была обновлена локация с id={}", locationEntity.getId());
        return updateLocation;
    }

    public void delete(Long id) {
        LocationEntity locationEntity = locationRepository.findById(id).orElseThrow(() ->
                new ConflictException("Лоакция с id = %s не найдена".formatted(id)));
        locationRepository.delete(locationEntity);

        log.info("Была удалена локация с id={}", locationEntity.getId());
    }
}
