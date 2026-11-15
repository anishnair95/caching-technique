package com.learning.caching.service.strategy;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import com.learning.caching.service.cache.ReadThroughCache;
import com.learning.caching.service.cache.WriteThroughCache;
import com.learning.caching.service.worker.WriteBackDatabaseWorker;
import org.springframework.stereotype.Service;

/** Serves products through a cache and persists updates asynchronously to the backing service. */
@Service
public class WriteBackProductService {

    private static final String CACHE_PREFIX =
            "write-back:product:";

    private final ProductService productService;
    private final ReadThroughCache readThroughCache;
    private final WriteThroughCache writeThroughCache;
    private final WriteBackDatabaseWorker databaseWorker;

    /**
     * Creates a write-back service using the product, cache, and persistence worker services.
     *
     * @param productService backing service for product operations
     * @param readThroughCache cache used to retrieve products on a miss
     * @param writeThroughCache cache used to update cached products
     * @param databaseWorker worker that asynchronously persists product updates
     */
    public WriteBackProductService(
            ProductService productService,
            ReadThroughCache readThroughCache,
            WriteThroughCache writeThroughCache,
            WriteBackDatabaseWorker databaseWorker) {

        this.productService = productService;
        this.readThroughCache = readThroughCache;
        this.writeThroughCache = writeThroughCache;
        this.databaseWorker = databaseWorker;
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
     * Updates the cache immediately and schedules persistence to the backing service asynchronously.
     *
     * @param id identifier of the product to update
     * @param request product data to apply
     * @return the updated product
     */
    public Product updateProduct(Long id, Product request) {

        String key = CACHE_PREFIX + id;

        // 1. Get current product, preserving fields such as createdAt
        Product updated = readThroughCache.get(
                key,
                Product.class,
                () -> productService.getProduct(id)
        );

        // 2. Apply requested changes
        updated.setName(request.getName());
        updated.setDescription(request.getDescription());
        updated.setPrice(request.getPrice());

        // 3. Update Redis immediately
        writeThroughCache.put(key, updated);

        // 4. Schedule database persistence asynchronously
        databaseWorker.persistProduct(id, updated);

        // 5. Return without waiting for MySQL
        return updated;
    }

}