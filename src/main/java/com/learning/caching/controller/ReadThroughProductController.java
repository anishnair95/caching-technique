package com.learning.caching.controller;

import com.learning.caching.entity.Product;
import com.learning.caching.service.strategy.ReadThroughProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/read-through/products")
public class ReadThroughProductController {

    private final ReadThroughProductService productService;

    public ReadThroughProductController(
            ReadThroughProductService productService) {

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

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}