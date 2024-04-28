package com.example.factory.service.implement;

import com.example.factory.dto.productivityInHour.ProductivityInHourDiagramResponseDto;
import com.example.factory.dto.productivityInHour.ProductivityInHourFilterDto;
import com.example.factory.dto.productivityInHour.ProductivityInHourResponseDto;
import com.example.factory.model.Product;
import com.example.factory.model.ProductivityInHour;
import com.example.factory.repository.ProductivityInHourRepository;
import com.example.factory.service.ProductivityInHourService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductivityInHourServiceImpl implements ProductivityInHourService {

    private final ProductivityInHourRepository productivityInHourRepository;

    public ProductivityInHourServiceImpl(ProductivityInHourRepository productivityInHourRepository) {
        this.productivityInHourRepository = productivityInHourRepository;
    }

    @Override
    @Transactional
    public ProductivityInHour create(ProductivityInHour productivityInHour) {
        var productivity = productivityInHourRepository
                .findByDateAndProduct_Id(
                        productivityInHour.getDate(),
                        productivityInHour.getProduct().getId());
        if (productivity.isPresent()) {
            throw new RuntimeException(String.format("Productivity for %s %s %s already exist",
                    productivityInHour.getProduct().getName(),
                    productivityInHour.getProduct().getMachine().getName(),
                    productivityInHour.getDate()));
        } else {
            return productivityInHourRepository.save(productivityInHour);
        }
    }

    @Override
    @Transactional
    public void delete(long productivityId) {
        getProductivityById(productivityId);
        productivityInHourRepository.deleteById(productivityId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductivityInHour getProductivity(Product product, LocalDateTime time) {
        try {
            return productivityInHourRepository.findByDateAndProduct_Id(time, product.getId())
                    .get();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Productivity for %s %s at %s not found", product.getName(), product.getMachine().getName(), time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProductivityInHour getProductivityById(Long productivityId) {
        var productivity = productivityInHourRepository.findById(productivityId);
        if (productivity.isPresent()) {
            return productivity.get();
        } else {
            throw new RuntimeException(String.format("Productivity with id %s not found", productivityId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductivityInHour> getAll() {
        return productivityInHourRepository.findAll();
    }

    @Override
    @Transactional
    public ProductivityInHour update(ProductivityInHour productivityInHour) {
        ProductivityInHour productivity = null;
        try {
            productivity = productivityInHourRepository
                    .findByDateAndProduct_Id(
                            productivityInHour.getDate(),
                            productivityInHour.getProduct().getId())
                    .get();
            productivity.setProdInHour(productivityInHour.getProdInHour());
        } catch (Exception e) {
            throw new RuntimeException(String.format("Productivity for %s %s %s doesn't exist",
                    productivityInHour.getProduct().getName(),
                    productivityInHour.getProduct().getMachine().getName(),
                    productivityInHour.getDate()));
        }
        return productivityInHourRepository.save(productivity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductivityInHour> findProductivityByDateAndProduct(ProductivityInHour productivityInHour) {
        return productivityInHourRepository.findByDateAndProduct_Id(productivityInHour.getDate(), productivityInHour.getProduct().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductivityInHour> getAllFiltered(ProductivityInHourFilterDto productivity) {
        return productivityInHourRepository.findByCriteria(productivity.getProductId(), productivity.getMachineId(), productivity.getDateTimeFrom(), productivity.getDateTimeTo(), productivity.getProdInHourFrom(), productivity.getProdInHourTo());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductivityInHourDiagramResponseDto> getAllFilteredDiagram(ProductivityInHourFilterDto productivity) {
        List<ProductivityInHourResponseDto> productivityDtoFromDb = productivityInHourRepository.findByCriteria(productivity.getProductId(), productivity.getMachineId(), productivity.getDateTimeFrom(), productivity.getDateTimeTo(), productivity.getProdInHourFrom(), productivity.getProdInHourTo()).stream()
                .map(p -> ProductivityInHourResponseDto.of(p))
                .collect(Collectors.toList());
        List<ProductivityInHourDiagramResponseDto> result = new ArrayList<>();
        int j = 0;
        List<String> dateTimes = new ArrayList<>();
        List<Integer> prodInHours = new ArrayList<>();
        String productName = "";
        if (productivityDtoFromDb.size() > 0) {
            productName = productivityDtoFromDb.get(0).getProductName();
            dateTimes.add(productivityDtoFromDb.get(0).getDateTimeString());
            prodInHours.add(productivityDtoFromDb.get(0).getProdInHour());
            for (int i = 1; i < productivityDtoFromDb.size(); i++) {
                ProductivityInHourResponseDto productivityInHourActual = productivityDtoFromDb.get(i);
                if (productivityInHourActual.getDateTime().minusHours(1).toString().equals(dateTimes.get(j)) && productivityInHourActual.getProductName().equals(productName)) {
                    dateTimes.add(productivityInHourActual.getDateTimeString());
                    prodInHours.add(productivityInHourActual.getProdInHour());
                    j++;
                    if (i == productivityDtoFromDb.size() - 1) {
                        result.add(ProductivityInHourDiagramResponseDto.builder()
                                .productName(productName)
                                .dateTimes(dateTimes)
                                .prodInHours(prodInHours)
                                .build());
                        dateTimes = new ArrayList<>();
                        prodInHours = new ArrayList<>();
                        productName = "";
                    }
                } else {
                    result.add(ProductivityInHourDiagramResponseDto.builder()
                            .productName(productName)
                            .dateTimes(dateTimes)
                            .prodInHours(prodInHours)
                            .build());
                    productName = productivityInHourActual.getProductName();
                    dateTimes = new ArrayList<>();
                    dateTimes.add(productivityInHourActual.getDateTimeString());
                    prodInHours = new ArrayList<>();
                    prodInHours.add(productivityInHourActual.getProdInHour());
                    j = 0;
                }
            }
            if (!productName.equals("")) {
                result.add(ProductivityInHourDiagramResponseDto.builder()
                        .productName(productName)
                        .dateTimes(dateTimes)
                        .prodInHours(prodInHours)
                        .build());
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductivityInHour> getAllFilteredPaged(ProductivityInHourFilterDto productivity, Pageable pageable) {
        return productivityInHourRepository.findByCriteriaPaged(productivity.getProductId(), productivity.getMachineId(), productivity.getDateTimeFrom(), productivity.getDateTimeTo(), productivity.getProdInHourFrom(), productivity.getProdInHourTo(), pageable);
    }
}
