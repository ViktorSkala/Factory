package com.example.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public ThreadGroup createThreadGroup() {
        return new ThreadGroup("ProductionThreads");
    }

    @Bean
    public Map<String, ProductGenerationThread> poolProductThreads() {
        return new HashMap<>();
    }
}
