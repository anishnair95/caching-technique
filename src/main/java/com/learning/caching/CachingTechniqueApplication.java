package com.learning.caching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstraps the caching technique Spring Boot application.
 */
@SpringBootApplication
public class CachingTechniqueApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command-line arguments passed to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(CachingTechniqueApplication.class, args);
    }

}
