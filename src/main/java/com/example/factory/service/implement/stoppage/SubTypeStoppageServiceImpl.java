package com.example.factory.service.implement.stoppage;

import com.example.factory.dto.stoppage.SubTypeStoppageDto;
import com.example.factory.model.stoppage.SubTypeStoppage;
import com.example.factory.repository.stoppage.BaseTypeStoppageRepository;
import com.example.factory.repository.stoppage.SubTypeStoppageRepository;
import com.example.factory.service.stoppage.SubTypeStoppageService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SubTypeStoppageServiceImpl implements SubTypeStoppageService {

    private SubTypeStoppageRepository subStoppageRepository;
    private BaseTypeStoppageRepository baseStoppageRepository;

    public SubTypeStoppageServiceImpl(SubTypeStoppageRepository subStoppageRepository, BaseTypeStoppageRepository baseStoppageRepository) {
        this.subStoppageRepository = subStoppageRepository;
        this.baseStoppageRepository = baseStoppageRepository;
    }

    @Override
    @Transactional
    public SubTypeStoppage create(SubTypeStoppageDto stoppageDto) {
        Optional<SubTypeStoppage> stoppageFromDb = subStoppageRepository.findByName(stoppageDto.getName());
        if (stoppageFromDb.isPresent()) {
            throw new EntityExistsException(String.format("Such type of stoppage %s already exists", stoppageDto.getName()));
        }
        SubTypeStoppage stoppage = SubTypeStoppage.of(stoppageDto);
        stoppage.setBaseTypeStoppage(baseStoppageRepository.findById(stoppageDto.getBaseTypeStoppageId()).get());
        return subStoppageRepository.save(stoppage);
    }

    @Override
    @Transactional(readOnly = true)
    public SubTypeStoppage read(long id) {
        return subStoppageRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Machine with id " + id + " not found"));
    }

    @Override
    @Transactional
    public SubTypeStoppage update(SubTypeStoppageDto stoppageDto) {
        read(stoppageDto.getId());
        SubTypeStoppage stoppage = SubTypeStoppage.of(stoppageDto);
        stoppage.setBaseTypeStoppage(baseStoppageRepository.findById(stoppageDto.getBaseTypeStoppageId()).get());
        Optional<SubTypeStoppage> stoppageFromDb = subStoppageRepository.findByName(stoppage.getName());
        if (stoppageFromDb.isPresent() && stoppageFromDb.get().getId() != stoppage.getId()) {
            throw new EntityExistsException(String.format("Such type of stoppage %s already exists", stoppage.getName()));
        }
        return subStoppageRepository.save(stoppage);
    }

    @Override
    @Transactional
    public void delete(long stoppageId) {
        read(stoppageId);
        subStoppageRepository.deleteById(stoppageId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubTypeStoppage> getAll() {
        return subStoppageRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubTypeStoppage> getAllByBaseTypeStoppage(long id) {
        return subStoppageRepository.findByBaseTypeStoppage_Id(id);
    }
}
