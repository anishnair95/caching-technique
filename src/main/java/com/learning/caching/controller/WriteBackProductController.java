package com.learning.caching.controller;

import com.learning.caching.entity.Product;
import com.learning.caching.service.strategy.WriteBackProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/write-back/products")
public class WriteBackProductController {

    private final WriteBackProductService productService;

    public WriteBackProductController(
            WriteBackProductService productService) {

        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productService.updateProduct(id, product);
    }
}