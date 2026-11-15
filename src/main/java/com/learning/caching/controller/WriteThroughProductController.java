package com.learning.caching.controller;

import com.learning.caching.entity.Product;
import com.learning.caching.service.strategy.WriteThroughProductService;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes product operations that use the write-through caching strategy.
 */
@RestController
@RequestMapping("/api/write-through/products")
public class WriteThroughProductController {

    private final WriteThroughProductService productService;

    /**
     * Creates a controller backed by the write-through product service.
     *
     * @param productService service that handles write-through product operations
     */
    public WriteThroughProductController(
            WriteThroughProductService productService) {

        this.productService = productService;
    }

    /**
     * Retrieves a product using the write-through caching strategy.
     *
     * @param id identifier of the product to retrieve
     * @return the requested product
     */
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }


    /**
     * Creates a product using the write-through caching strategy.
     *
     * @param product product to create
     * @return the created product
     */
    @PostMapping
    public Product createProduct(
            @RequestBody Product product) {

        return productService.createProduct(product);
    }


    /**
     * Updates a product using the write-through caching strategy.
     *
     * @param id identifier of the product to update
     * @param product product values to apply
     * @return the updated product
     */
    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productService.updateProduct(id, product);
    }

    /**
     * Deletes a product using the write-through caching strategy.
     *
     * @param id identifier of the product to delete
     */
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}