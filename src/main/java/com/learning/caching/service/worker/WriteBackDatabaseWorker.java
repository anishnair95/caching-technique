package com.learning.caching.service.worker;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Persists product updates asynchronously as part of the write-back cache strategy.
 */
@Component
public class WriteBackDatabaseWorker {

    private static final Logger log =
            LoggerFactory.getLogger(WriteBackDatabaseWorker.class);

    private final ProductService productService;

    /**
     * Creates a worker that persists products through the product service.
     *
     * @param productService database-backed product operations
     */
    public WriteBackDatabaseWorker(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Persists a product update asynchronously using the configured write-back executor.
     *
     * @param id the product identifier to update
     * @param product the product values to persist
     */
    @Async("writeBackExecutor")
    public void persistProduct(Long id, Product product) {

        log.info(
                "BACKGROUND WRITE STARTED: product={}, thread={}",
                id,
                Thread.currentThread().getName()
        );

        try {
            Thread.sleep(5000);
            productService.updateProduct(id, product);

            log.info(
                    "BACKGROUND WRITE COMPLETED: product={}",
                    id
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Background write interrupted: product={}", id, e);

        } catch (Exception e) {
            log.error(
                    "BACKGROUND WRITE FAILED: product={}",
                    id,
                    e
            );
        }
    }
}