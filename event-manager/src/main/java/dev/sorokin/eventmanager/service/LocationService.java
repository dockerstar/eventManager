package dev.sorokin.eventmanager.service;

import dev.sorokin.eventmanager.dto.LocationDto;
import dev.sorokin.eventmanager.dto.LocationDtoMapper;
import dev.sorokin.eventmanager.entity.LocationEntity;
import dev.sorokin.eventmanager.exception.NoSuchFoundException;
import dev.sorokin.eventmanager.model.Location;
import dev.sorokin.eventmanager.entity.LocationEntityMapper;
import dev.sorokin.eventmanager.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;


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

    @Cacheable(
            value = "locations"
    )
    @Transactional(readOnly = true)
    public List<Location> findAll() {
        return locationRepository.findAll().stream()
                .map(locationEntityMapper::toDomain)
                .toList();
    }


    @Cacheable(
            value = "location",
            key = "#id"
    )
    @Transactional(readOnly = true)
    public Location findById(Long id) {
        LocationEntity locationEntity = locationRepository.findById(id).orElseThrow(() ->
                new NoSuchFoundException("Лоакция с id = %s не найдена".formatted(id)));

        log.info("Был осущетвлен поиск локации с id={} из базы", locationEntity.getId());

        var locationSavedBD = locationEntityMapper.toDomain(locationEntity);

        return locationSavedBD;
    }

    @CacheEvict(
            value = "location",
            key = "#id"
    )
    @Transactional()
    public Location update(Long id, LocationDto locationDto) {
        LocationEntity locationEntity = locationRepository.findById(id).orElseThrow(() ->
                new NoSuchFoundException("Лоакция с id = %s не найдена".formatted(id)));
        locationEntity.setName(locationDto.name());
        locationEntity.setAddress(locationDto.address());
        locationEntity.setCapacity(locationDto.capacity());
        locationEntity.setDescription(locationDto.description());

        log.info("Была обновлена локация с id={}", locationEntity.getId());
        locationRepository.save(locationEntity);
        return locationEntityMapper.toDomain(locationEntity);
    }

    @CacheEvict(
            value = "location",
            key = "#id"
    )
    public void delete(Long id) {
        LocationEntity locationEntity = locationRepository.findById(id).orElseThrow(() ->
                new NoSuchFoundException("Лоакция с id = %s не найдена".formatted(id)));
        locationRepository.delete(locationEntity);

        log.info("Была удалена локация с id={} из базы ", locationEntity.getId());
    }
}
