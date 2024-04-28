package com.example.factory.controller;

import com.example.factory.dto.stoppage.BaseTypeStoppageDto;
import com.example.factory.model.stoppage.BaseTypeStoppage;
import com.example.factory.service.stoppage.BaseTypeStoppageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/base_type_stoppage")
@PreAuthorize("hasAuthority('Engineer')")
public class BaseTypeStoppageController {

    private final BaseTypeStoppageService stoppageService;

    public BaseTypeStoppageController(BaseTypeStoppageService stoppageService) {
        this.stoppageService = stoppageService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody BaseTypeStoppage stoppage) {
        return ResponseEntity.ok().body(BaseTypeStoppageDto.of(stoppageService.create(stoppage)));
    }

    @PreAuthorize("hasAuthority('Worker')")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") long stoppageId) {
        return ResponseEntity.ok().body(BaseTypeStoppageDto.of(stoppageService.read(stoppageId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody BaseTypeStoppage stoppage) {
        return ResponseEntity.ok().body(BaseTypeStoppageDto.of(stoppageService.update(stoppage)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long stoppageId) {
        stoppageService.delete(stoppageId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('Worker')")
    public ResponseEntity<List<BaseTypeStoppageDto>> getAll() {
        return ResponseEntity.ok().body(stoppageService.getAll().stream()
                .map(s -> BaseTypeStoppageDto.of(s))
                .collect(Collectors.toList()));
    }
}
