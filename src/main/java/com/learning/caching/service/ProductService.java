
package com.learning.caching.service;

import com.learning.caching.entity.Product;
import com.learning.caching.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// TODO abstract class
// multiple implementation
// 1. DB based
// 2. Cache aside based
// 3. Read-through based
/**
 * Provides application-level operations for products.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Creates a service backed by the given product repository.
     *
     * @param productRepository repository used to persist and retrieve products
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Persists a new product.
     *
     * @param product the product to create
     * @return the persisted product
     */
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Retrieves a product by its identifier.
     *
     * @param id the product identifier
     * @return the matching product
     */
    @Transactional(readOnly = true)
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
    }

    /**
     * Retrieves all products.
     *
     * @return all persisted products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Updates the name, description, and price of an existing product.
     *
     * @param id the identifier of the product to update
     * @param updatedProduct product providing the updated values
     * @return the updated persisted product
     */
    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {

        Product existing = getProduct(id);

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());

        return productRepository.save(existing);
    }

    /**
     * Deletes the product with the given identifier.
     *
     * @param id the identifier of the product to delete
     */
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
