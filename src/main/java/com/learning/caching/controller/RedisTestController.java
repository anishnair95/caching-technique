
package com.learning.caching.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes endpoints for storing, retrieving, and deleting Redis string values.
 */
@RestController
@RequestMapping("/api/redis")
public class RedisTestController {

    private final StringRedisTemplate redisTemplate;

    /**
     * Creates a controller backed by the Redis string template.
     *
     * @param redisTemplate template used to access Redis string values
     */
    public RedisTestController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Stores a string value in Redis under the given key.
     *
     * @param key Redis key to store the value under
     * @param value string value to store
     * @return confirmation that the value was stored
     */
    @PostMapping("/{key}")
    public String set(
            @PathVariable String key,
            @RequestParam String value) {

        redisTemplate.opsForValue().set(key, value);

        return "Value stored in Redis";
    }

    /**
     * Retrieves the string value stored under the given key.
     *
     * @param key Redis key to retrieve
     * @return the stored string value, or {@code null} if no value exists
     */
    @GetMapping("/{key}")
    public String get(@PathVariable String key) {

        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Deletes the value stored under the given key.
     *
     * @param key Redis key to delete
     * @return confirmation that the key was deleted
     */
    @DeleteMapping("/{key}")
    public String delete(@PathVariable String key) {

        redisTemplate.delete(key);

        return "Key deleted";
    }
}
