package dev.sorokin.eventmanager.exception;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;

@Slf4j
public class CustomCacheException implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.error("Cache error for get key={}, errMessage={}", key, exception.getMessage());
        if (exception instanceof RedisConnectionFailureException) {
            log.error("Redis not connection: " + exception.getMessage());
        }
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value) {
        log.error("Cache error for put key={}, errMessage={}", key, exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.error("Cache error for del key={}, errMessage={}", key, exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.error("Cache error for clear cache={}, errMessage={}", cache.getName(), exception.getMessage());
    }
}
