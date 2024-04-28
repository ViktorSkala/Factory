package com.example.factory.repository;

import com.example.factory.model.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    Optional<Machine> findByName(@NonNull String name);

}
