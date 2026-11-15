package com.learning.caching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configures the single-thread executor used for asynchronous write-back tasks.
 *
 * <p>One worker processes submitted tasks in order in this lab, avoiding
 * concurrent updates overwriting each other out of order. This is a learning
 * simplification, not a multi-instance ordering guarantee.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Creates the executor for asynchronous write-back tasks.
     *
     * @return the initialized single-thread write-back executor
     */
    @Bean(name = "writeBackExecutor")
    public Executor writeBackExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        // One worker thread to preserve order in this lab
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);

        // Maximum pending tasks
        executor.setQueueCapacity(1000);

        executor.setThreadNamePrefix("write-back-");

        executor.initialize();

        return executor;
    }
}