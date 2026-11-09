package dev.sorokin.eventmanager.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventmanager.exception.CustomCacheException;
import dev.sorokin.eventmanager.model.Location;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.w3c.dom.events.Event;

import java.time.Duration;

@EnableCaching
@Configuration
public class CacheConf implements CachingConfigurer {

    @Value("${redis.field.ttl}")
    private Integer REDIS_FIELD_TTL;

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory
    ) {
        var genericSerializer = new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(genericSerializer))
                .entryTtl(Duration.ofSeconds(REDIS_FIELD_TTL));



        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(configuration)
                .transactionAware()
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CustomCacheException();
    }
}
