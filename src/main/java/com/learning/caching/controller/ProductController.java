
package com.learning.caching.controller;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes the standard product creation, retrieval, update, and deletion endpoints.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Creates a controller backed by the product service.
     *
     * @param productService service that handles product operations
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Creates a product.
     *
     * @param product product to create
     * @return the created product
     */
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    /**
     * Retrieves a product by its identifier.
     *
     * @param id identifier of the product to retrieve
     * @return the requested product
     */
    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    /**
     * Retrieves all products.
     *
     * @return all products
     */
    @GetMapping
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    /**
     * Updates a product by its identifier.
     *
     * @param id identifier of the product to update
     * @param product product values to apply
     * @return the updated product
     */
    @PutMapping("/{id}")
    public Product update(
            @PathVariable Long id,
            @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    /**
     * Deletes a product by its identifier.
     *
     * @param id identifier of the product to delete
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
