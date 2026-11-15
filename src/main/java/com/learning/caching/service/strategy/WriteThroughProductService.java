package com.learning.caching.service.strategy;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import com.learning.caching.service.cache.ReadThroughCache;
import com.learning.caching.service.cache.WriteThroughCache;
import org.springframework.stereotype.Service;

/**
 * Applies a write-through caching strategy to product operations.
 * Reads use read-through loading; successful creates and updates refresh Redis,
 * and successful deletions evict the corresponding cache entry.
 */
@Service
public class WriteThroughProductService {

    private static final String CACHE_PREFIX =
            "write-through:product:";

    private final ProductService productService;
    private final ReadThroughCache readThroughCache;
    private final WriteThroughCache writeThroughCache;

    /**
     * Creates a product service using database persistence and read/write-through caches.
     *
     * @param productService database-backed product operations
     * @param readThroughCache cache used to load and retrieve products
     * @param writeThroughCache cache used to update and evict products
     */
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
     *
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
     * Creates a product in the database, then caches the persisted product in Redis.
     *
     * @param product the product values to persist
     * @return the created product
     */
    public Product createProduct(Product product) {

        // Persist first so the generated ID is available for the cache key.
        Product created =

                productService.createProduct(product);

        // Cache the persisted value in Redis.
        String key = CACHE_PREFIX + created.getId();

        writeThroughCache.put(key, created);

        // Return the database result.
        return created;
    }

    /**
     * Updates a product in the database, then refreshes its Redis cache entry.
     * The cache is updated only after the database update succeeds.
     *
     * @param id the product identifier
     * @param product the product values to persist
     * @return the updated product
     */
    public Product updateProduct(Long id, Product product) {

        // Persist first; update the cache only after the database succeeds.
        Product updated =
                productService.updateProduct(id, product);

        // Cache the persisted value in Redis.
        String key = CACHE_PREFIX + id;

        writeThroughCache.put(key, updated);

        // Return the database result.
        return updated;
    }

    /**
     * Deletes a product from the database, then evicts its Redis cache entry.
     * The cache entry is evicted only after the database deletion succeeds.
     *
     * @param id the product identifier
     */
    public void deleteProduct(Long id) {

        // Delete first; retain the cache entry if the database operation fails.
        productService.deleteProduct(id);

        // 2. Remove cached value
        writeThroughCache.evict(CACHE_PREFIX + id);
    }
}