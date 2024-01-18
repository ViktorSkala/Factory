package com.example.factory.service.implement.stoppage;

import com.example.factory.dto.stoppage.StoppageFilterDto;
import com.example.factory.model.Employee;
import com.example.factory.model.Machine;
import com.example.factory.model.Product;
import com.example.factory.model.stoppage.BaseTypeStoppage;
import com.example.factory.model.stoppage.Stoppage;
import com.example.factory.model.stoppage.SubTypeStoppage;
import com.example.factory.repositoty.stoppage.StoppageRepository;
import com.example.factory.service.stoppage.StoppageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Service
public class StoppageServiceImpl implements StoppageService {

    private StoppageRepository stoppageRepository;

    public StoppageServiceImpl(StoppageRepository stoppageRepository) {
        this.stoppageRepository = stoppageRepository;
    }

    @Override
    public Stoppage create(Stoppage stoppage) {
//        Optional<Stoppage> stoppageFromDb = stoppageRepository.findByName(stoppage.getName());
//        if (machineFromDb.isPresent()) {
//            throw new RuntimeException(String.format("Machine %s already exists", machine.getName()));
//        }
        return stoppageRepository.save(stoppage);
    }

    @Override
    @Transactional(readOnly = true)
    public Stoppage read(long id) {
        return stoppageRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Stoppage with id " + id + " not found"));
    }

    @Override
    public Stoppage update(Stoppage stoppage) {
        read(stoppage.getId());
        return stoppageRepository.save(stoppage);
    }

    @Override
    public void delete(long stoppageId) {
        read(stoppageId);
        stoppageRepository.deleteById(stoppageId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stoppage> getAllFiltered() {
        return stoppageRepository.findAll();
    }

    @Override
    public List<Stoppage> getAllFiltered(StoppageFilterDto stoppageDto) {
        Stoppage probe = new Stoppage();
        Field[] fields = stoppageDto.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            try {
                field.setAccessible(true);
                if (field.getName().equals("productId") && field.getLong(stoppageDto) != 0) {
                    probe.setProduct(new Product(field.getLong(stoppageDto)));
                } else if (field.getName().equals("machineId") && field.getLong(stoppageDto) != 0) {
                    probe.setMachine(new Machine(field.getLong(stoppageDto)));
                } else if (field.getName().equals("ownerId") && field.getLong(stoppageDto) != 0) {
                    probe.setOwner(new Employee(field.getLong(stoppageDto)));
                } else if (field.getName().equals("subTypeStoppageId") && field.getLong(stoppageDto) != 0) {
                    probe.setSubTypeStoppage(new SubTypeStoppage(field.getLong(stoppageDto)));
                } else if (field.getName().equals("baseTypeStoppageId") && field.getLong(stoppageDto) != 0) {
                    probe.setBaseTypeStoppage(new BaseTypeStoppage(field.getLong(stoppageDto)));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("product.expectedProductivity");
        Example<Stoppage> example = Example.of(probe, matcher);
        return stoppageRepository.findAll(example);
    }

    @Override
    public List<Stoppage> getAllByMachineId(long machineId) {
        return stoppageRepository.findByMachine_Id(machineId);
    }

    @Override
    public List<Stoppage> getAllByProductId(long productId) {
        return stoppageRepository.findByProduct_Id(productId);
    }

    @Override
    public List<Stoppage> getAllBySubTypeStoppage(long subTypeStoppageId) {
        return stoppageRepository.findBySubTypeStoppage_Id(subTypeStoppageId);
    }

    @Override
    public List<Stoppage> getAllByBaseTypeStoppage(long baseTypeStoppageId) {
        return stoppageRepository.findByBaseTypeStoppage_Id(baseTypeStoppageId);
    }

    @Override
    public Page<Stoppage> findEntitiesByDynamicCriteriaPaged(StoppageFilterDto stoppageDto, Pageable pageable) {
        return stoppageRepository.findByCriteriaPaged(stoppageDto.getProductId(), stoppageDto.getMachineId(), stoppageDto.getBaseTypeStoppageId(), stoppageDto.getSubTypeStoppageId(), stoppageDto.getStartDate(), stoppageDto.getEndDate(), stoppageDto.getDurationStart(), stoppageDto.getDurationEnd(), pageable);
    }

    @Override
    public List<Stoppage> findEntitiesByDynamicCriteria(StoppageFilterDto stoppageDto) {
        return stoppageRepository.findByCriteria(stoppageDto.getProductId(), stoppageDto.getMachineId(), stoppageDto.getBaseTypeStoppageId(), stoppageDto.getSubTypeStoppageId(), stoppageDto.getStartDate(), stoppageDto.getEndDate(), stoppageDto.getDurationStart(), stoppageDto.getDurationEnd());
    }

    @Override
    public List<Stoppage> findByCriteria2(StoppageFilterDto stoppage) {
        return stoppageRepository.findByStartDateAfterAndEndDateBefore(stoppage.getStartDate(), stoppage.getEndDate());
    }
}
