package com.learning.caching.service.strategy;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import com.learning.caching.service.cache.ReadThroughCache;
import org.springframework.stereotype.Service;

/** Writes products to the backing service without populating the cache and uses read-through retrieval. */
@Service
public class WriteAroundProductService {

    private static final String CACHE_PREFIX =
            "write-around:product:";

    private final ProductService productService;
    private final ReadThroughCache readThroughCache;

    /**
     * Creates a write-around service using the backing product service and read-through cache.
     *
     * @param productService backing service for product operations
     * @param readThroughCache cache used for product retrieval and invalidation
     */
    public WriteAroundProductService(
            ProductService productService,
            ReadThroughCache readThroughCache) {

        this.productService = productService;
        this.readThroughCache = readThroughCache;
    }

    /**
     * Creates a product in the backing service without populating the cache.
     *
     * @param product product data to create
     * @return the created product
     */
    public Product createProduct(Product product) {

        // Write directly to MySQL.
        // Do not populate Redis.
        return productService.createProduct(product);
    }

    /**
     * Retrieves a product through the cache, loading and caching it on a miss.
     *
     * @param id identifier of the product to retrieve
     * @return the requested product
     */
    public Product getProduct(Long id) {

        String key = CACHE_PREFIX + id;

        // Read from Redis.
        // On a miss, load from MySQL and populate Redis.
        return readThroughCache.get(
                key,
                Product.class,
                () -> productService.getProduct(id)
        );
    }

    /**
     * Updates a product in the backing service and evicts its cached value.
     *
     * @param id identifier of the product to update
     * @param product product data to apply
     * @return the updated product
     */
    public Product updateProduct(Long id, Product product) {

        // 1. Update MySQL
        Product updated =
                productService.updateProduct(id, product);

        // 2. Invalidate old cached value
        readThroughCache.evict(CACHE_PREFIX + id);

        // Do not populate Redis with the updated product.
        return updated;
    }

    /**
     * Deletes a product from the backing service and evicts its cached value.
     *
     * @param id identifier of the product to delete
     */
    public void deleteProduct(Long id) {

        // 1. Delete from MySQL
        productService.deleteProduct(id);

        // 2. Invalidate Redis
        readThroughCache.evict(CACHE_PREFIX + id);
    }
}