package com.learning.caching;

import org.springframework.boot.SpringApplication;

public class TestCachingTechniqueApplication {

    public static void main(String[] args) {
        SpringApplication.from(CachingTechniqueApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
