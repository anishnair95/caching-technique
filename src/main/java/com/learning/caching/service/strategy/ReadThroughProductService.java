package com.learning.caching.service.strategy;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import com.learning.caching.service.cache.ReadThroughCache;
import org.springframework.stereotype.Service;

/** Provides product operations using a read-through cache for retrieval. */
@Service
public class ReadThroughProductService {

    private static final String CACHE_PREFIX = "read-through:product:";

    private final ReadThroughCache readThroughCache;
    private final ProductService productService;

    /**
     * Creates a service using the read-through cache and backing product service.
     *
     * @param readThroughCache cache that loads products on a miss
     * @param productService backing service for product operations
     */
    public ReadThroughProductService(
            ReadThroughCache readThroughCache,
            ProductService productService) {

        this.readThroughCache = readThroughCache;
        this.productService = productService;
    }

    /**
     * Retrieves a product through the cache, loading it from the backing service on a miss.
     *
     * @param id identifier of the product to retrieve
     * @return the requested product
     */
    public Product getProduct(Long id) {

        String key = CACHE_PREFIX + id;

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

        // Update MySQL first
        Product updated =
                productService.updateProduct(id, product);

        // Invalidate cache after successful DB update
        readThroughCache.evict(CACHE_PREFIX + id);

        return updated;
    }

    /**
     * Deletes a product from the backing service and evicts its cached value.
     *
     * @param id identifier of the product to delete
     */
    public void deleteProduct(Long id) {

        // Delete from MySQL first
        productService.deleteProduct(id);

        // Invalidate cache
        readThroughCache.evict(CACHE_PREFIX + id);
    }
}