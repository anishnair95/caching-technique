package com.learning.caching.service.strategy;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import com.learning.caching.service.cache.ReadThroughCache;
import org.springframework.stereotype.Service;

@Service
public class ReadThroughProductService {

    private static final String CACHE_PREFIX = "read-through:product:";

    private final ReadThroughCache readThroughCache;
    private final ProductService productService;

    public ReadThroughProductService(
            ReadThroughCache readThroughCache,
            ProductService productService) {

        this.readThroughCache = readThroughCache;
        this.productService = productService;
    }

    public Product getProduct(Long id) {

        String key = CACHE_PREFIX + id;

        return readThroughCache.get(
                key,
                Product.class,
                () -> productService.getProduct(id)
        );
    }

    public Product updateProduct(Long id, Product product) {

        // Update MySQL first
        Product updated =
                productService.updateProduct(id, product);

        // Invalidate cache after successful DB update
        readThroughCache.evict(CACHE_PREFIX + id);

        return updated;
    }

    public void deleteProduct(Long id) {

        // Delete from MySQL first
        productService.deleteProduct(id);

        // Invalidate cache
        readThroughCache.evict(CACHE_PREFIX + id);
    }
}