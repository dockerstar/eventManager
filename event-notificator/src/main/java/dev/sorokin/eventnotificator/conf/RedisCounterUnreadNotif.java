package dev.sorokin.eventnotificator.conf;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCounterUnreadNotif {

    private final StringRedisTemplate redisTemplate;

    public void addUnreadNotifForUser(Long userId) {
        String key = "notif:unread:"+userId;

        try {
            redisTemplate.opsForValue().increment(key);
            log.info("REDIS - Created field unread notifications for user={}", userId);
        } catch (RedisConnectionFailureException | QueryTimeoutException ex) {
            log.error("Redis not connection " + ex.getMessage());
        }

    }

    public void updateUnreadNotifForUser(Long userId, Long count) {
        String key = "notif:unread:"+userId;

        try {
            redisTemplate.opsForValue().set(key, String.valueOf(count));
            log.info("REDIS - Updated field unread notifications for user={}", userId);
        } catch (RedisConnectionFailureException | QueryTimeoutException ex) {
            log.error("Redis not connection " + ex.getMessage());
        }

    }
}
