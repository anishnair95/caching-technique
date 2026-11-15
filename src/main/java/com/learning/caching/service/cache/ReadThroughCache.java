package com.learning.caching.service.cache;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Provides Redis-backed read-through caching for values serialized as JSON.
 *
 * <p>Cache entries use a fixed 10-minute time-to-live. Invalid cached JSON is
 * evicted, then the loader is called to retrieve a fresh value. Values that
 * cannot be serialized are returned but not cached.
 */
@Component
public class ReadThroughCache {

    private static final Logger log =
            LoggerFactory.getLogger(ReadThroughCache.class);

    private static final Duration CACHE_TTL =
            Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Creates a read-through cache backed by Redis and the supplied JSON mapper.
     *
     * @param redisTemplate Redis operations used to read, write, and evict cache entries
     * @param objectMapper mapper used to deserialize and serialize cached values
     */
    public ReadThroughCache(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper) {

        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Returns the cached value for {@code key}, or loads and caches a value
     * when no valid cached value exists.
     *
     * @param <T> the type of object to cache
     * @param key the Redis key
     * @param type the class used to deserialize cached JSON
     * @param loader the supplier that loads the value on a cache miss
     * @return the cached or loaded value, or {@code null} if the loader returns
     *         {@code null}
     * @throws NullPointerException if {@code key}, {@code type}, or
     *         {@code loader} is {@code null}
     */
    public <T> T get(
            String key,
            Class<T> type,
            Supplier<T> loader) {

        // 1. Check Redis
        String cachedValue =
                redisTemplate.opsForValue().get(key);

        // 2. Return cached value on hit
        if (cachedValue != null) {
            try {
                log.info("READ-THROUGH CACHE HIT: {}", key);

                return objectMapper.readValue(
                        cachedValue,
                        type
                );

            } catch (JacksonException e) {
                log.warn("Invalid cache data for key: {}", key, e);
                redisTemplate.delete(key);
            }
        }

        // 3. Cache miss: invoke the loader
        log.info("READ-THROUGH CACHE MISS: {}", key);

        T value = loader.get();

        // 4. Populate Redis
        if (value != null) {
            try {
                String json =
                        objectMapper.writeValueAsString(value);

                redisTemplate.opsForValue().set(
                        key,
                        json,
                        CACHE_TTL
                );

                log.info("READ-THROUGH CACHE POPULATED: {}", key);

            } catch (JacksonException e) {
                log.error("Could not serialize value for key: {}", key, e);
            }
        }

        return value;
    }

    /**
     * Removes the cache entry associated with {@code key}.
     *
     * @param key the Redis key to evict
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public void evict(String key) {
        redisTemplate.delete(key);
        log.info("READ-THROUGH CACHE EVICTED: {}", key);
    }
}