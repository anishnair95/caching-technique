package com.learning.caching.repository;

import com.learning.caching.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides persistence operations for {@link Product} entities.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

}
