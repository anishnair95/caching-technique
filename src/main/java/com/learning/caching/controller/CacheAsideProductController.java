package com.learning.caching.controller;

import com.learning.caching.entity.Product;
import com.learning.caching.service.strategy.CacheAsideProductService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes product operations that use the cache-aside strategy.
 */
@RestController
@RequestMapping("/api/cache-aside/products")
public class CacheAsideProductController {

    private final CacheAsideProductService cacheService;

    /**
     * Creates a controller backed by the cache-aside product service.
     *
     * @param cacheService service that handles cache-aside product operations
     */
    public CacheAsideProductController(
            CacheAsideProductService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Retrieves a product using the cache-aside strategy.
     *
     * @param id identifier of the product to retrieve
     * @return the requested product
     */
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return cacheService.getProduct(id);
    }

    /**
     * Updates a product using the cache-aside strategy.
     *
     * @param id identifier of the product to update
     * @param product product values to apply
     * @return the updated product
     */
    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return cacheService.updateProduct(id, product);
    }

    /**
     * Deletes a product using the cache-aside strategy.
     *
     * @param id identifier of the product to delete
     */
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        cacheService.deleteProduct(id);
    }
}
