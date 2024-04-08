package com.example.factory;

import com.example.factory.exception.ProductionThreadException;
import com.example.factory.model.Product;
import com.example.factory.model.State;
import com.example.factory.model.stoppage.Stoppage;
import com.example.factory.service.MachineService;
import com.example.factory.service.ProductService;
import com.example.factory.service.ProductivityInHourService;
import com.example.factory.service.ProductivityInMinuteService;
import com.example.factory.service.stoppage.StoppageService;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ThreadManager {

    private ThreadGroup threadGroup;
    private Map<String, ProductGenerationThread> poolProductThreads;
    private ProductService productService;
    private MachineService machineService;
    private ProductivityInMinuteService productivityInMinuteService;
    private ProductivityInHourService productivityInHourService;
    private StoppageService stoppageService;

    public ThreadManager(ThreadGroup threadGroup, Map<String, ProductGenerationThread> poolProductThreads, ProductService productService, MachineService machineService, ProductivityInMinuteService productivityInMinuteService, ProductivityInHourService productivityInHourService, StoppageService stoppageService) {
        this.threadGroup = threadGroup;
        this.poolProductThreads = poolProductThreads;
        this.productService = productService;
        this.machineService = machineService;
        this.productivityInMinuteService = productivityInMinuteService;
        this.productivityInHourService = productivityInHourService;
        this.stoppageService = stoppageService;
    }

    public ProductGenerationThread startProduction(Long productId) throws ProductionThreadException {
        Product product = productService.read(productId);
        String machineName = product.getMachine().getName();
        String threadName = product.getName() + " " + product.getNumbersInPack() + " " + machineName;
        if (checkThreadExist(machineName)) {
            throw new ProductionThreadException(String.format("Production %s already run", threadName));
        }
        ProductGenerationThread productionThread = ProductGenerationThread.builder()
                .name(threadName)
                .state(State.RUN)
                .product(product)
                .productivityInMinuteService(productivityInMinuteService)
                .productivityInHourService(productivityInHourService)
                .build();
        poolProductThreads.put(threadName, productionThread);
        Thread thread = new Thread(threadGroup, productionThread, threadName);
        thread.setDaemon(true);
        thread.start();
        return productionThread;
    }

    public Stoppage pauseProduction(String threadName) throws ProductionThreadException {
        if (!checkThreadExist(threadName)) {
            throw new ProductionThreadException(String.format("Production %s doesn't run", threadName));
        }
        ProductGenerationThread productionThread = poolProductThreads.get(threadName);
        if (productionThread.getState().toString().equals("PAUSED")) {
            throw new ProductionThreadException(String.format("Production %s already paused", threadName));
        }
        Stoppage stoppage = new Stoppage();
        stoppage.setProduct(productService.read(productionThread.getProduct().getId()));
        stoppage.setMachine(machineService.read(productionThread.getProduct().getMachine().getId()));
        try {
            stoppage = stoppageService.create(stoppage);
        } catch (Exception e) {
            throw new ProductionThreadException(e.getMessage());
        }
        productionThread.setState(State.PAUSED);
        return stoppage;
    }

    public ProductGenerationThread resumeProduction(String threadName) throws ProductionThreadException {
        if (!checkThreadExist(threadName)) {
            throw new ProductionThreadException(String.format("Production %s doesn't run", threadName));
        }
        ProductGenerationThread productionThread = poolProductThreads.get(threadName);
        if (productionThread.getState().toString().equals("RUN")) {
            throw new ProductionThreadException(String.format("Production %s already run", threadName));
        }
        productionThread.setState(State.RUN);
        return productionThread;
    }

    public void finishProduction(String threadName) throws ProductionThreadException {
        if (!checkThreadExist(threadName)) {
            throw new ProductionThreadException(String.format("Production %s doesn't run", threadName));
        }
        ProductGenerationThread productionThread = poolProductThreads.get(threadName);
        if (productionThread.getState().toString().equals("PAUSED")) {
            throw new ProductionThreadException(String.format("Production %s can't be finished while it paused", threadName));
        }
        productionThread.setState(State.FINISHED);
        poolProductThreads.remove(threadName);
    }

    public ProductGenerationThread getInfo(String threadName) throws ProductionThreadException {
        if (!checkThreadExist(threadName)) {
            throw new ProductionThreadException(String.format("Production %s doesn't exist", threadName));
        }
        ProductGenerationThread thread = poolProductThreads.get(threadName);
        return thread;
    }

    public List<ProductGenerationThread> getAllProductionThreads() {
        return poolProductThreads.values().stream().collect(Collectors.toList());
    }

    private boolean checkThreadExist(String threadName) {
        Thread[] threads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        return Arrays.stream(threads)
                .anyMatch(t -> t.getName().contains(threadName));
    }
}
