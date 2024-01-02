package com.example.factory.controller;

import com.example.factory.ProductGenerationThread;
import com.example.factory.dto.ProductionManagerDTO;
import com.example.factory.dto.ProductionThreadDto;
import com.example.factory.model.Product;
import com.example.factory.model.State;
import com.example.factory.service.ProductService;
import com.example.factory.service.MachineService;
import com.example.factory.service.ProductivityInHourService;
import com.example.factory.service.ProductivityInMinuteService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/production")
public class ProductionController {

    private ProductivityInMinuteService productivityInMinuteService;
    private ProductivityInHourService productivityInHourService;
    private ProductService productService;
    private MachineService machineService;
    private final ThreadGroup threadGroup;
    private final Map<String, ProductGenerationThread> poolProductThreads;

    public ProductionController(ProductivityInMinuteService productivityInMinuteService, ProductivityInHourService productivityInHourService, ProductService productService, MachineService machineService, ThreadGroup threadGroup, Map<String, ProductGenerationThread> poolProductThreads) {
        this.productivityInMinuteService = productivityInMinuteService;
        this.productivityInHourService = productivityInHourService;
        this.productService = productService;
        this.machineService = machineService;
        this.threadGroup = threadGroup;
        this.poolProductThreads = poolProductThreads;
    }

    @GetMapping("/all")
    public List<ProductionThreadDto> allProductions() {
        return poolProductThreads.entrySet().stream()
                .map(s -> ProductionThreadDto.of(s.getKey(), s.getValue().getState().toString()))
                .collect(Collectors.toList());
    }

    @GetMapping("/state/{thread_name}")
    public String getState(@PathVariable("thread_name") String threadName) {
        if (!checkThreadExist(threadName)) {
            throw new RuntimeException(String.format("Production %s doesn't run",threadName));
        }
        ProductGenerationThread thread = poolProductThreads.get(threadName);
        return thread.getState().toString();
    }

    @PostMapping("/start")
    public String create(@RequestBody ProductionManagerDTO productionDTO) {
        Product product = productService.read(productionDTO.getProductId());
        String machineName = product.getMachine().getName();
        String threadName = product.getName() + " " + product.getNumbersInPack() + " " + machineName;
        if (checkThreadExist(machineName)) {
            throw new RuntimeException(String.format("Production %s already run",threadName));
        }
        ProductGenerationThread productionThread = ProductGenerationThread.builder()
                .state(State.RUN)
                .product(product)
                .productivityInMinuteService(productivityInMinuteService)
                .productivityInHourService(productivityInHourService)
                .build();
        poolProductThreads.put(threadName, productionThread);
        Thread thread = new Thread(threadGroup, productionThread, threadName);
        thread.setDaemon(true);
        thread.start();
        return thread.getName();
    }

    private boolean checkThreadExist(String threadName) {
        Thread[] threads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        return Arrays.stream(threads)
                .anyMatch(t -> t.getName().contains(threadName));
    }

    @GetMapping("/wait/{thread_name}")
    public ProductionThreadDto pauseThread(@PathVariable("thread_name") String threadName) throws InterruptedException {
        if (!checkThreadExist(threadName)) {
            throw new RuntimeException(String.format("Production %s doesn't run",threadName));
        }
        ProductGenerationThread thread = poolProductThreads.get(threadName);
        if (thread.getState().equals("PAUSED")) {
            throw new RuntimeException(String.format("Production %s already paused",threadName));
        }
        thread.setState(State.PAUSED);
        System.out.println(threadName + " paused");
        ProductionThreadDto result = ProductionThreadDto.of(threadName, thread.getState().toString());
        System.out.println("Wait: " + result.toString());
        return result;
    }

    @GetMapping("/resume/{thread_name}")
    public ProductionThreadDto notifyThread(@PathVariable("thread_name") String threadName) {
        if (!checkThreadExist(threadName)) {
            throw new RuntimeException(String.format("Production %s doesn't run",threadName));
        }
        ProductGenerationThread thread = poolProductThreads.get(threadName);
        if (thread.getState().equals("RUN")) {
            throw new RuntimeException(String.format("Production %s already run",threadName));
        }
        thread.setState(State.RUN);
        System.out.println(threadName + " notify");
        return ProductionThreadDto.of(threadName, thread.getState().toString());
    }

    @GetMapping("/finish/{thread_name}")
    public String finishThread(@PathVariable("thread_name") String threadName) throws InterruptedException {
        if (!checkThreadExist(threadName)) {
            throw new RuntimeException(String.format("Production %s doesn't run",threadName));
        }
        ProductGenerationThread threadBase = poolProductThreads.get(threadName);
        Thread thread = getThreadByName(threadName);
        threadBase.setState(State.FINISHED);
        poolProductThreads.remove(threadName);
        System.out.println(thread.getName() + " finished");
        return thread.getName();
    }

    private Thread getThreadByName(String threadName) {
        Thread[] threads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        return Arrays.stream(threads)
                .filter(t -> t.getName().equals(threadName))
                .findFirst()
                .get();
    }
}