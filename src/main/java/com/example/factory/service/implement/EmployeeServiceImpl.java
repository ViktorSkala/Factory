package com.example.factory.service.implement;

import com.example.factory.model.Employee;
import com.example.factory.repository.EmployeeRepository;
import com.example.factory.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public Employee create(Employee employee) {
        Employee fromDB = employeeRepository.findByEmail(employee.getEmail());
        if (!Objects.isNull(fromDB)) {
            throw new RuntimeException(String.format("Employee with email: %s already exists", employee.getEmail()));
        }
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee read(long id) {
        return employeeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Employee with id " + id + " not found"));
    }

    @Override
    @Transactional
    public Employee update(Employee employee) {
        read(employee.getId());
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void delete(long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
}
