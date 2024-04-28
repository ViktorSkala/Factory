package com.example.factory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FactoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(FactoryApplication.class, args);
        Thread thread1 = Thread.currentThread();
        System.out.println("Thread name " + thread1.getName());
    }
}
