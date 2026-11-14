
package com.learning.caching.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis")
public class RedisTestController {

    private final StringRedisTemplate redisTemplate;

    public RedisTestController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/{key}")
    public String set(
            @PathVariable String key,
            @RequestParam String value) {

        redisTemplate.opsForValue().set(key, value);

        return "Value stored in Redis";
    }

    @GetMapping("/{key}")
    public String get(@PathVariable String key) {

        return redisTemplate.opsForValue().get(key);
    }

    @DeleteMapping("/{key}")
    public String delete(@PathVariable String key) {

        redisTemplate.delete(key);

        return "Key deleted";
    }
}
