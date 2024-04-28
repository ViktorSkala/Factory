package com.example.factory.service.implement;

import com.example.factory.dto.productivityInMinute.ProductivityInMinuteFilterDto;
import com.example.factory.model.Product;
import com.example.factory.model.ProductivityInMinute;
import com.example.factory.repository.ProductivityInMinuteRepository;
import com.example.factory.service.ProductivityInMinuteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ProductivityInMinuteServiceImpl implements ProductivityInMinuteService {

    private final ProductivityInMinuteRepository productivityInMinuteRepository;

    public ProductivityInMinuteServiceImpl(ProductivityInMinuteRepository productivityInMinuteRepository) {
        this.productivityInMinuteRepository = productivityInMinuteRepository;
    }

    @Override
    @Transactional
    public ProductivityInMinute create(ProductivityInMinute productivityInMinute) {
        var productivity = productivityInMinuteRepository
                .findByDateAndProduct_Id(
                        productivityInMinute.getDate(),
                        productivityInMinute.getProduct().getId());
        if (productivity.isPresent()) {
            throw new RuntimeException(String.format("Productivity for %s %s %s already exist",
                    productivityInMinute.getProduct().getName(),
                    productivityInMinute.getProduct().getMachine().getName(),
                    productivityInMinute.getDate()));
        } else {
            return productivityInMinuteRepository.save(productivityInMinute);
        }
    }

    @Override
    @Transactional
    public void delete(long productivityId) {
        getProductivityById(productivityId);
        productivityInMinuteRepository.deleteById(productivityId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductivityInMinute getProductivity(Product product, LocalDateTime time) {
        var productivity = productivityInMinuteRepository.findByDateAndProduct_Id(time, product.getId());
        if (productivity.isPresent()) {
            return productivity.get();
        } else {
            throw new RuntimeException(String.format("Productivity for %s %s at %s not found", product.getName(), product.getMachine().getName(), time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductivityInMinute getProductivityById(Long productivityId) {
        var productivity = productivityInMinuteRepository.findById(productivityId);
        if (productivity.isPresent()) {
            return productivity.get();
        } else {
            throw new RuntimeException(String.format("Productivity with id %s not found", productivityId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductivityInMinute> getAll() {
        return productivityInMinuteRepository.findAll();
    }

    @Override
    @Transactional
    public ProductivityInMinute update(ProductivityInMinute productivityInMinute) {
        ProductivityInMinute productivity = null;
        try {
            productivity = productivityInMinuteRepository
                    .findByDateAndProduct_Id(
                            productivityInMinute.getDate(),
                            productivityInMinute.getProduct().getId())
                    .get();
            productivity.setProdInMinute(productivityInMinute.getProdInMinute());
        } catch (Exception e) {
            throw new RuntimeException(String.format("Productivity for %s %s %s doesn't exist",
                    productivityInMinute.getProduct().getName(),
                    productivityInMinute.getProduct().getMachine().getName(),
                    productivityInMinute.getDate()));
        }
        return productivityInMinuteRepository.save(productivity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductivityInMinute> findProductivityByDateAndProduct(ProductivityInMinute productivityInMinute) {
        return productivityInMinuteRepository.findByDateAndProduct_Id(productivityInMinute.getDate(), productivityInMinute.getProduct().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductivityInMinute> getAllFiltered(ProductivityInMinuteFilterDto productivity) {
        return productivityInMinuteRepository.findByCriteria(productivity.getProductId(), productivity.getMachineId(), productivity.getDateTimeFrom(), productivity.getDateTimeTo(), productivity.getProdInMinuteFrom(), productivity.getProdInMinuteTo());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductivityInMinute> getAllFilteredPaged(ProductivityInMinuteFilterDto productivity, Pageable pageable) {
        return productivityInMinuteRepository.findByCriteriaPaged(productivity.getProductId(), productivity.getMachineId(), productivity.getDateTimeFrom(), productivity.getDateTimeTo(), productivity.getProdInMinuteFrom(), productivity.getProdInMinuteTo(), pageable);
    }
}
