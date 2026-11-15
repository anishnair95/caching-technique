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

@RestController
@RequestMapping("/api/cache-aside/products")
public class CacheAsideProductController {

    private final CacheAsideProductService cacheService;

    public CacheAsideProductController(
            CacheAsideProductService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return cacheService.getProduct(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return cacheService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        cacheService.deleteProduct(id);
    }
}
