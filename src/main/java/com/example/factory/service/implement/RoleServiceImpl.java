package com.example.factory.service.implement;

import com.example.factory.model.Role;
import com.example.factory.repository.RoleRepository;
import com.example.factory.service.RoleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public Role create(Role role) {
        Role fromDB = roleRepository.findByName(role.getName());
        if (!Objects.isNull(fromDB)) {
            throw new RuntimeException(String.format("Role with name: %s already exists", role.getName()));
        }
        return roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = true)
    public Role read(long id) {
        return roleRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Role with id " + id + " not found"));
    }

    @Override
    @Transactional
    public Role update(Role role) {
        read(role.getId());
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void delete(long id) {
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
