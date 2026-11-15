package com.learning.caching.service.cache;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

/**
 * Writes serialized values to Redis with a fixed 10-minute time-to-live.
 * Serialization failures are reported as {@link IllegalStateException}.
 */
@Component
public class WriteThroughCache {

    private static final Logger log =
            LoggerFactory.getLogger(WriteThroughCache.class);

    private static final Duration CACHE_TTL =
            Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public WriteThroughCache(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper) {

        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Serializes {@code value} as JSON and stores it under {@code key}.
     *
     * @param <T> the type of value to cache
     * @param key the Redis key
     * @param value the value to serialize and cache
     * @throws IllegalStateException if the value cannot be serialized
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public <T> void put(String key, T value) {

        try {
            String json = objectMapper.writeValueAsString(value);

            redisTemplate.opsForValue().set(
                    key,
                    json,
                    CACHE_TTL
            );

            log.info("WRITE-THROUGH CACHE UPDATED: {}", key);

        } catch (JacksonException e) {
            log.error("Could not serialize value for key: {}", key, e);
            throw new IllegalStateException(
                    "Failed to update cache", e
            );
        }
    }

    /**
     * Removes the cache entry associated with {@code key}.
     *
     * @param key the Redis key to evict
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public void evict(String key) {
        redisTemplate.delete(key);
        log.info("WRITE-THROUGH CACHE EVICTED: {}", key);
    }
}