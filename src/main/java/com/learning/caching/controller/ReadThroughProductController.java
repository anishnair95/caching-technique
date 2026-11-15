package com.learning.caching.controller;

import com.learning.caching.entity.Product;
import com.learning.caching.service.strategy.ReadThroughProductService;
import org.springframework.web.bind.annotation.*;

/**
 * Exposes product operations that use the read-through caching strategy.
 */
@RestController
@RequestMapping("/api/read-through/products")
public class ReadThroughProductController {

    private final ReadThroughProductService productService;

    /**
     * Creates a controller backed by the read-through product service.
     *
     * @param productService service that handles read-through product operations
     */
    public ReadThroughProductController(
            ReadThroughProductService productService) {

        this.productService = productService;
    }

    /**
     * Retrieves a product using the read-through caching strategy.
     *
     * @param id identifier of the product to retrieve
     * @return the requested product
     */
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    /**
     * Updates a product using the read-through caching strategy.
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
     * Deletes a product using the read-through caching strategy.
     *
     * @param id identifier of the product to delete
     */
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}