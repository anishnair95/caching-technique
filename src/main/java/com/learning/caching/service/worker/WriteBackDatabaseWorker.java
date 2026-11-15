package com.learning.caching.service.worker;

import com.learning.caching.entity.Product;
import com.learning.caching.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WriteBackDatabaseWorker {

    private static final Logger log =
            LoggerFactory.getLogger(WriteBackDatabaseWorker.class);

    private final ProductService productService;

    public WriteBackDatabaseWorker(ProductService productService) {
        this.productService = productService;
    }

    /**
     * The @Async annotation tells Spring to execute this method using the configured executor instead of blocking the caller.
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