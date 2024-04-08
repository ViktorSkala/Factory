package com.example.factory;

import com.example.factory.model.Product;
import com.example.factory.model.ProductivityInHour;
import com.example.factory.model.ProductivityInMinute;
import com.example.factory.model.State;
import com.example.factory.service.MachineService;
import com.example.factory.service.ProductService;
import com.example.factory.service.ProductivityInHourService;
import com.example.factory.service.ProductivityInMinuteService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductGenerationThread implements Runnable {

    private String name;
    private State state;
    private Product product;
    private ProductivityInMinuteService productivityInMinuteService;
    private ProductivityInHourService productivityInHourService;

    private LocalDateTime currentTime;
    private LocalDateTime currentMinute;
    private LocalDateTime currentHour;

    private int counterPerMinute;
    private int counterPerHour;
    private int counterPerWorkShift;

    @Override
    public void run() {
        System.out.println("Thread " + name + " started");
        currentTime = LocalDateTime.now();
        currentMinute = currentTime.plusMinutes(1).withSecond(0).withNano(0);
        currentHour = currentTime.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        while (!state.toString().equals("FINISHED")) {
            currentTime = LocalDateTime.now();
            if (currentTime.isBefore(currentMinute)) {
                generateUnit();
            } else {
                saveProductivityInMinute();
                if (!currentTime.isBefore(currentHour)) {
                    saveProductivityInHour();
                }
            }
        }
        saveProductivityInMinute();
        saveProductivityInHour();
        Thread.currentThread().interrupt();
    }

    private long sleepDuration(Product product) {
        long timeForProduction = 60000 / product.getExpectedProductivity();
        return timeForProduction + ThreadLocalRandom.current().nextLong(-timeForProduction / 15, timeForProduction / 10);
    }

    private void generateUnit() {
        if (!state.toString().equals("PAUSED")) {
            counterPerMinute++;
            System.out.println(counterPerMinute);
            try {
                Thread.sleep(sleepDuration(product));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveProductivityInMinute() {
        var productivityInMinute = ProductivityInMinute.builder()
                .product(product)
                .machine(product.getMachine())
                .date(currentMinute)
                .prodInMinute(counterPerMinute)
                .build();
        Optional<ProductivityInMinute> productivityFromDb = productivityInMinuteService.findProductivityByDateAndProduct(productivityInMinute);
        if (productivityFromDb.isPresent()) {
            productivityInMinute.setProdInMinute(productivityInMinute.getProdInMinute() + productivityFromDb.get().getProdInMinute());
            productivityInMinuteService.update(productivityInMinute);
        } else {
            productivityInMinuteService.create(productivityInMinute);
        }
        System.out.println(Thread.currentThread().getName());
        System.out.println(product.getName() + " " + productivityInMinute.getDate() + " - " + productivityInMinute.getProdInMinute());
        currentMinute = currentMinute.plusMinutes(1);
        counterPerHour += counterPerMinute;
        counterPerMinute = 0;
    }

    private void saveProductivityInHour() {
        var productivityInHour = ProductivityInHour.builder()
                .product(product)
                .machine(product.getMachine())
                .date(currentHour)
                .prodInHour(counterPerHour)
                .build();
        Optional<ProductivityInHour> productivityFromDb = productivityInHourService.findProductivityByDateAndProduct(productivityInHour);
        if (productivityFromDb.isPresent()) {
            productivityInHour.setProdInHour(productivityInHour.getProdInHour() + productivityFromDb.get().getProdInHour());
            productivityInHourService.update(productivityInHour);
        } else {
            productivityInHourService.create(productivityInHour);
        }
        System.out.println(Thread.currentThread().getName());
        System.out.println(product.getName() + " " + productivityInHour.getDate() + " - " + productivityInHour.getProdInHour());
        currentHour = currentHour.plusHours(1);
        counterPerHour = 0;
    }
}
