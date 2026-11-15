package com.learning.caching.service.strategy;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

/** Reads products from Redis when available and otherwise loads them from the product service. */
@Service
public class CacheAsideProductService {

    private static final Logger log =
            LoggerFactory.getLogger(CacheAsideProductService.class);

    private static final String CACHE_PREFIX = "product:";
    private static final Duration   CACHE_TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ProductService productService;

    /**
     * Creates a cache-aside product service using Redis, JSON mapping, and the backing product service.
     *
     * @param redisTemplate Redis access for cached product values
     * @param objectMapper mapper used to serialize and deserialize products
     * @param productService backing service for product operations
     */
    public CacheAsideProductService(
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper,
            ProductService productService) {

        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.productService = productService;
    }

    /**
     * Returns a product from Redis or loads and caches it on a cache miss.
     *
     * @param id identifier of the product to retrieve
     * @return the requested product
     */
    public Product getProduct(Long id) {

        String cacheKey = CACHE_PREFIX + id;

        // 1. Check Redis
        String cachedValue =
                redisTemplate.opsForValue().get(cacheKey);

        // 2. Cache hit
        if (cachedValue != null) {
            try {
                log.info("CACHE HIT for key: {}", cacheKey);

                return objectMapper.readValue(
                        cachedValue,
                        Product.class
                );

            } catch (JacksonException e) {
                // Remove invalid cached data
                log.warn("Invalid cache value for key: {}", cacheKey, e);
                redisTemplate.delete(cacheKey);
            }
        }

        // 3. Cache miss: fetch from MySQL
        log.info("CACHE MISS for key: {}", cacheKey);

        Product product = productService.getProduct(id);

        // 4. Populate Redis
        try {
            String json = objectMapper.writeValueAsString(product);

            redisTemplate.opsForValue().set(
                    cacheKey,
                    json,
                    CACHE_TTL
            );

            log.info("Product cached: {}", cacheKey);

        } catch (JacksonException e) {
            log.error("Could not serialize product: {}", id, e);
        }

        // 5. Return product
        return product;
    }

    /**
     * Updates a product in the backing service and invalidates its cached value.
     *
     * @param id identifier of the product to update
     * @param product product data to apply
     * @return the updated product
     */
    public Product updateProduct(Long id, Product product) {

        // Update MySQL first
        Product updated = productService.updateProduct(id, product);

        // Invalidate Redis after DB update succeeds
        String cacheKey = CACHE_PREFIX + id;

        redisTemplate.delete(cacheKey);

        log.info("Cache invalidated: {}", cacheKey);

        return updated;
    }

    /**
     * Deletes a product from the backing service and invalidates its cached value.
     *
     * @param id identifier of the product to delete
     */
    public void deleteProduct(Long id) {

        // Delete from MySQL
        productService.deleteProduct(id);

        // Invalidate Redis
        String cacheKey = CACHE_PREFIX + id;

        redisTemplate.delete(cacheKey);

        log.info("Cache invalidated: {}", cacheKey);
    }

}
