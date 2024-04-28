package com.example.factory.service.implement.stoppage;

import com.example.factory.model.stoppage.BaseTypeStoppage;
import com.example.factory.repository.stoppage.BaseTypeStoppageRepository;
import com.example.factory.service.stoppage.BaseTypeStoppageService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BaseTypeStoppageServiceImpl implements BaseTypeStoppageService {

    private BaseTypeStoppageRepository stoppageRepository;

    public BaseTypeStoppageServiceImpl(BaseTypeStoppageRepository stoppageRepository) {
        this.stoppageRepository = stoppageRepository;
    }

    @Override
    @Transactional
    public BaseTypeStoppage create(BaseTypeStoppage stoppage) {
        Optional<BaseTypeStoppage> stoppageFromDb = stoppageRepository.findByName(stoppage.getName());
        if (stoppageFromDb.isPresent()) {
            throw new EntityExistsException(String.format("Such type of stoppage %s already exists", stoppage.getName()));
        }
        return stoppageRepository.save(stoppage);
    }

    @Override
    @Transactional(readOnly = true)
    public BaseTypeStoppage read(long id) {
        return stoppageRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Machine with id " + id + " not found"));
    }

    @Override
    @Transactional
    public BaseTypeStoppage update(BaseTypeStoppage stoppage) {
        read(stoppage.getId());
        Optional<BaseTypeStoppage> stoppageFromDb = stoppageRepository.findByName(stoppage.getName());
        if (stoppageFromDb.isPresent() && stoppageFromDb.get().getId() != stoppage.getId()) {
            throw new EntityExistsException(String.format("Such type of stoppage %s already exists", stoppage.getName()));
        }
        return stoppageRepository.save(stoppage);
    }

    @Override
    @Transactional
    public void delete(long stoppageId) {
        stoppageRepository.deleteById(stoppageId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BaseTypeStoppage> getAll() {
        return stoppageRepository.findAll();
    }
}
