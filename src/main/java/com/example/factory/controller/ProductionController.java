package com.example.factory.controller;

import com.example.factory.ProductGenerationThread;
import com.example.factory.ThreadManager;
import com.example.factory.dto.ProductionManagerDTO;
import com.example.factory.dto.ProductionThreadDto;
import com.example.factory.dto.stoppage.StoppageCreationResponseDto;
import com.example.factory.exception.ProductionThreadException;
import com.example.factory.service.ProductService;
import com.example.factory.service.MachineService;
import com.example.factory.service.ProductivityInHourService;
import com.example.factory.service.ProductivityInMinuteService;
import com.example.factory.service.stoppage.StoppageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/production")
public class ProductionController {

    private ThreadManager threadManager;

    public ProductionController(ThreadManager threadManager) {
        this.threadManager = threadManager;
    }

    @GetMapping("/all")
    public List<ProductionThreadDto> allProductions() {
        return threadManager.getAllProductionThreads().stream()
                .map(s -> ProductionThreadDto.of(s))
                .collect(Collectors.toList());
    }

    @GetMapping("/info/{thread_name}")
    public ResponseEntity<?> getThreadState(@PathVariable("thread_name") String threadName) {
        try {
            return ResponseEntity.ok().body(ProductionThreadDto.of(threadManager.getInfo(threadName)));
        } catch (ProductionThreadException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/start")
    public ResponseEntity<?> create(@RequestBody ProductionManagerDTO productionDTO) {
        try {
            return ResponseEntity.ok().body(ProductionThreadDto.of(threadManager.startProduction(productionDTO.getProductId())));
        } catch (ProductionThreadException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/wait/{thread_name}")
    public ResponseEntity<?> pauseThread(@PathVariable("thread_name") String threadName) {
        try {
            return ResponseEntity.ok().body(StoppageCreationResponseDto.of(threadManager.pauseProduction(threadName)));
        } catch (ProductionThreadException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/resume/{thread_name}")
    public ResponseEntity<?> resumeThread(@PathVariable("thread_name") String threadName) {
        try {
            return ResponseEntity.ok().body(ProductionThreadDto.of(threadManager.resumeProduction(threadName)));
        } catch (ProductionThreadException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/finish/{thread_name}")
    public ResponseEntity<?> finishThread(@PathVariable("thread_name") String threadName) {
        try {
            threadManager.finishProduction(threadName);
            return ResponseEntity.ok().build();
        } catch (ProductionThreadException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
