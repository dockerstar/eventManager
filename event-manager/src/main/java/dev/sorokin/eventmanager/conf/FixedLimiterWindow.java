package dev.sorokin.eventmanager.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

@Slf4j
@Component
public class FixedLimiterWindow {

    private final StringRedisTemplate redisTemplate;

    public FixedLimiterWindow(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean limiterWindow(
            String clientId,
            Long limit,
            Duration ttl
    ) {
        long window = System.currentTimeMillis() / ttl.toMillis();
        String key = String.format("rate:%s:%s", clientId, window);

        Long countReq = redisTemplate.opsForValue().increment(key);

        if (countReq != null && countReq == 1L) {
            redisTemplate.expire(key, ttl);
        }

        return countReq != null && countReq <= limit;
    }
}
