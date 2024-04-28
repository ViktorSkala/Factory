package com.example.factory.repository;

import com.example.factory.model.ProductivityInMinute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductivityInMinuteRepository extends JpaRepository<ProductivityInMinute, Long> {
    @Query("select p from ProductivityInMinute p where p.date = ?1 and p.product.id = ?2")
    Optional<ProductivityInMinute> findByDateAndProduct_Id(LocalDateTime date, Long id);

    @Query("""
            select p from ProductivityInMinute p
            where (:productId is null or p.product.id = :productId) 
            and (:machineId is null or p.machine.id = :machineId) 
            and (:dayTimeFrom <= p.date) 
            and (:dayTimeTo >= p.date)
            and (:prodInMinuteFrom <= p.prodInMinute) 
            and (:prodInMinuteTo >= p.prodInMinute) 
            ORDER BY p.date ASC
            """)
    Page<ProductivityInMinute> findByCriteriaPaged(@Param("productId") Long productId,
                                                   @Param("machineId") Long machineId,
                                                   @Param("dayTimeFrom") LocalDateTime dateFrom,
                                                   @Param("dayTimeTo") LocalDateTime dateTo,
                                                   @Param("prodInMinuteFrom") Long prodInMinuteFrom,
                                                   @Param("prodInMinuteTo") Long prodInMinuteTo,
                                                   Pageable pageable);

    @Query("""
            select p from ProductivityInMinute p
            where (:productId is null or p.product.id = :productId) 
            and (:machineId is null or p.machine.id = :machineId) 
            and (:dayTimeFrom <= p.date) 
            and (:dayTimeTo >= p.date)
            and (:prodInMinuteFrom <= p.prodInMinute) 
            and (:prodInMinuteTo >= p.prodInMinute) 
            ORDER BY p.date ASC
            """)
    List<ProductivityInMinute> findByCriteria(@Param("productId") Long productId,
                                              @Param("machineId") Long machineId,
                                              @Param("dayTimeFrom") LocalDateTime dateFrom,
                                              @Param("dayTimeTo") LocalDateTime dateTo,
                                              @Param("prodInMinuteFrom") Long prodInMinuteFrom,
                                              @Param("prodInMinuteTo") Long prodInMinuteTo);
}
