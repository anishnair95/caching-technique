package com.learning.caching.service.strategy;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import com.learning.caching.service.cache.ReadThroughCache;
import com.learning.caching.service.cache.WriteThroughCache;
import org.springframework.stereotype.Service;

/**
 * Applies a write-through caching strategy to product operations.
 * Reads use {@link ReadThroughCache}; successful database updates are written
 * to Redis, and successful deletions evict the corresponding cache entry.
 */
@Service
public class WriteThroughProductService {

    private static final String CACHE_PREFIX =
            "write-through:product:";

    private final ProductService productService;
    private final ReadThroughCache readThroughCache;
    private final WriteThroughCache writeThroughCache;

    public WriteThroughProductService(
            ProductService productService,
            ReadThroughCache readThroughCache,
            WriteThroughCache writeThroughCache) {

        this.productService = productService;
        this.readThroughCache = readThroughCache;
        this.writeThroughCache = writeThroughCache;
    }

    /**
     * Retrieves a product from Redis, loading it from the database on a cache miss.
     * Read-through cache mechanism used
     * @param id the product identifier
     * @return the matching product
     */
    public Product getProduct(Long id) {

        String key = CACHE_PREFIX + id;

        // Read from Redis, load from MySQL on a miss
        return readThroughCache.get(
                key,
                Product.class,
                () -> productService.getProduct(id)
        );
    }

    /**
     * Updates a product in the database, then refreshes its Redis cache entry.
     * The cache is updated only after the database update succeeds.
     * Write-though cache mechanism used here
     * @param id the product identifier
     * @param product the product values to persist
     * @return the updated product
     */
    public Product updateProduct(Long id, Product product) {

        // 1. Update MySQL first
        Product updated =
                productService.updateProduct(id, product);

        // 2. Update Redis with the latest DB value
        String key = CACHE_PREFIX + id;

        writeThroughCache.put(key, updated);

        // 3. Return updated product
        return updated;
    }

    /**
     * Deletes a product from the database, then evicts its Redis cache entry.
     * The cache entry is evicted only after the database deletion succeeds.
     *
     * @param id the product identifier
     */
    public void deleteProduct(Long id) {

        // 1. Delete from MySQL
        productService.deleteProduct(id);

        // 2. Remove cached value
        writeThroughCache.evict(CACHE_PREFIX + id);
    }
}