package com.learning.caching.service.strategy;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import com.learning.caching.service.cache.ReadThroughCache;
import com.learning.caching.service.cache.WriteThroughCache;
import com.learning.caching.service.worker.WriteBackDatabaseWorker;
import org.springframework.stereotype.Service;

@Service
public class WriteBackProductService {

    private static final String CACHE_PREFIX =
            "write-back:product:";

    private final ProductService productService;
    private final ReadThroughCache readThroughCache;
    private final WriteThroughCache writeThroughCache;
    private final WriteBackDatabaseWorker databaseWorker;

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

    public Product getProduct(Long id) {

        String key = CACHE_PREFIX + id;

        return readThroughCache.get(
                key,
                Product.class,
                () -> productService.getProduct(id)
        );
    }

    /**
     * Redis is updated before the background task is scheduled. The application doesn't wait for the worker to finish.
     *
     * Write sequence
     * writeThroughCache.put(key, updated);
     *
     * databaseWorker.persistProduct(id, updated);
     *
     * return updated;
     * @param id
     * @param request
     * @return
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