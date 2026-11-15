package com.learning.caching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync

/**
 * Why one thread?
 *
 * Suppose two updates happen:
 * Update 1: Price = 2000
 * Update 2: Price = 2500
 *
 *
 * If two worker threads process these updates concurrently, Update 1 might finish after Update 2 and overwrite the newer database value.
 *
 * Using one worker thread in our lab ensures tasks are processed in submission order. This is a learning simplification, not a complete multi-instance ordering guarantee.
 */
public class AsyncConfig {

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