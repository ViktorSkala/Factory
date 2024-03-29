package com.example.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class appConfig {

//    @Bean
//    public ProductName productName() {
//        return ProductName.ANY_NAME;
//    }

    @Bean
    public ThreadGroup createThreadGroup() {
        return new ThreadGroup("ProductionThreads");
    }

    @Bean
    public Map<String, ProductGenerationThread> poolProductThreads() {
        return new HashMap<>();
    }
}
