package com.example.factory.service.implement;

import com.example.factory.model.Machine;
import com.example.factory.repository.MachineRepository;
import com.example.factory.service.MachineService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MachineServiceImpl implements MachineService {

    private MachineRepository machineRepository;

    public MachineServiceImpl(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Override
    @Transactional
    public Machine create(Machine machine) {
        Optional<Machine> machineFromDb = machineRepository.findByName(machine.getName());
        if (machineFromDb.isPresent()) {
            throw new RuntimeException(String.format("Machine %s already exists", machine.getName()));
        }
        return machineRepository.save(machine);
    }

    @Override
    @Transactional(readOnly = true)
    public Machine read(long id) {
        return machineRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Machine with id " + id + " not found"));
    }

    @Override
    @Transactional
    public Machine update(Machine machine) {
        read(machine.getId());
        Optional<Machine> machineFromDb = machineRepository.findByName(machine.getName());
        if (machineFromDb.isPresent() && machineFromDb.get().getId() != machine.getId()) {
            throw new EntityExistsException(String.format("Machine with name %s already exists", machine.getName()));
        }
        return machineRepository.save(machine);
    }

    @Override
    @Transactional
    public void delete(long machineId) {
        read(machineId);
        machineRepository.deleteById(machineId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Machine> getAll() {
        return machineRepository.findAll();
    }
}
