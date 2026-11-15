package com.learning.caching.service.strategy;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import com.learning.caching.service.cache.ReadThroughCache;
import org.springframework.stereotype.Service;

@Service
public class WriteAroundProductService {

    private static final String CACHE_PREFIX =
            "write-around:product:";

    private final ProductService productService;
    private final ReadThroughCache readThroughCache;

    public WriteAroundProductService(
            ProductService productService,
            ReadThroughCache readThroughCache) {

        this.productService = productService;
        this.readThroughCache = readThroughCache;
    }

    public Product createProduct(Product product) {

        // Write directly to MySQL.
        // Do not populate Redis.
        return productService.createProduct(product);
    }

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

    public Product updateProduct(Long id, Product product) {

        // 1. Update MySQL
        Product updated =
                productService.updateProduct(id, product);

        // 2. Invalidate old cached value
        readThroughCache.evict(CACHE_PREFIX + id);

        // Do not populate Redis with the updated product.
        return updated;
    }

    public void deleteProduct(Long id) {

        // 1. Delete from MySQL
        productService.deleteProduct(id);

        // 2. Invalidate Redis
        readThroughCache.evict(CACHE_PREFIX + id);
    }
}