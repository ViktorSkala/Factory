package com.example.factory.repository;

import com.example.factory.model.ProductivityInHour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductivityInHourRepository extends JpaRepository<ProductivityInHour, Long> {

    @Query("select p from ProductivityInHour p where p.date = ?1 and p.product.id = ?2")
    Optional<ProductivityInHour> findByDateAndProduct_Id(LocalDateTime date, Long id);

    @Query("""
            select p from ProductivityInHour p
            where (:productId is null or p.product.id = :productId) 
            and (:machineId is null or p.machine.id = :machineId) 
            and (:dayTimeFrom <= p.date) 
            and (:dayTimeTo >= p.date)
            and (:prodInHourFrom <= p.prodInHour) 
            and (:prodInHourTo >= p.prodInHour) 
            ORDER BY p.date ASC
            """)
    Page<ProductivityInHour> findByCriteriaPaged(@Param("productId") Long productId,
                                                   @Param("machineId") Long machineId,
                                                   @Param("dayTimeFrom") LocalDateTime dateFrom,
                                                   @Param("dayTimeTo") LocalDateTime dateTo,
                                                   @Param("prodInHourFrom") Long prodInMinuteFrom,
                                                   @Param("prodInHourTo") Long prodInMinuteTo,
                                                   Pageable pageable);

    @Query("""
            select p from ProductivityInHour p
            where (:productId is null or p.product.id = :productId) 
            and (:machineId is null or p.machine.id = :machineId) 
            and (:dayTimeFrom <= p.date) 
            and (:dayTimeTo >= p.date)
            and (:prodInHourFrom is null or :prodInHourFrom <= p.prodInHour) 
            and (:prodInHourTo is null or :prodInHourTo >= p.prodInHour) 
            ORDER BY p.id ASC
            """)
    List<ProductivityInHour> findByCriteria(@Param("productId") Long productId,
                                              @Param("machineId") Long machineId,
                                              @Param("dayTimeFrom") LocalDateTime dateFrom,
                                              @Param("dayTimeTo") LocalDateTime dateTo,
                                              @Param("prodInHourFrom") Long prodInHourFrom,
                                              @Param("prodInHourTo") Long prodInHourTo);

}
