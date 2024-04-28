package com.example.factory.repository.stoppage;

import com.example.factory.model.stoppage.Stoppage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface StoppageRepository extends JpaRepository<Stoppage, Long> {
    @Query("select s from Stoppage s where s.startDate > ?1 and s.endDate < ?2")
    List<Stoppage> findByStartDateAfterAndEndDateBefore(LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
            select s from Stoppage s
            where (:productId is null or s.product.id = :productId) 
            and (:machineId is null or s.machine.id = :machineId) 
            and (:baseStopId is null or s.baseTypeStoppage.id = :baseStopId ) 
            and (:subStopId is null or s.subTypeStoppage.id = :subStopId) 
            and (:startDay <= s.startDate) 
            and (:endDay >= s.startDate)
            and (:durationStart <= s.duration) 
            and (:durationEnd >= s.duration) 
            ORDER BY s.startDate ASC
            """)
    Page<Stoppage> findByCriteriaPaged(@Param("productId") Long productId,
                                       @Param("machineId") Long machineId,
                                       @Param("baseStopId") Long baseStopId,
                                       @Param("subStopId") Long subStopId,
                                       @Param("startDay") LocalDateTime startDate,
                                       @Param("endDay") LocalDateTime endDate,
                                       @Param("durationStart") Duration durationStart,
                                       @Param("durationEnd") Duration durationEnd,
                                       Pageable pageable);

    @Query("""
            select s from Stoppage s
            where (:productId is null or s.product.id = :productId) 
            and (:machineId is null or s.machine.id = :machineId) 
            and (:baseStopId is null or s.baseTypeStoppage.id = :baseStopId ) 
            and (:subStopId is null or s.subTypeStoppage.id = :subStopId) 
            and (:startDay <= s.startDate) 
            and (:endDay >= s.startDate)
            and (:durationStart <= s.duration) 
            and (:durationEnd >= s.duration) 
            ORDER BY s.startDate ASC
            """)
    List<Stoppage> findByCriteria(@Param("productId") Long productId,
                                  @Param("machineId") Long machineId,
                                  @Param("baseStopId") Long baseStopId,
                                  @Param("subStopId") Long subStopId,
                                  @Param("startDay") LocalDateTime startDate,
                                  @Param("endDay") LocalDateTime endDate,
                                  @Param("durationStart") Duration durationStart,
                                  @Param("durationEnd") Duration durationEnd);

    @Query("""
            SELECT s FROM Stoppage s 
            WHERE s.duration IS NULL 
            AND (:productId is null or s.product.id = :productId) 
            AND (:machineId is null or s.machine.id = :machineId) 
            """)
    List<Stoppage> findNotFinishedStoppage(@Param("productId") Long productId,
                                           @Param("machineId") Long machineId);

    @Query("select s from Stoppage s where s.baseTypeStoppage.id = ?1")
    List<Stoppage> findByBaseTypeStoppage_Id(Long id);

    @Query("select s from Stoppage s where s.subTypeStoppage.id = ?1")
    List<Stoppage> findBySubTypeStoppage_Id(Long id);

    @Query("select s from Stoppage s where s.product.id = ?1")
    List<Stoppage> findByProduct_Id(Long id);

    @Query("select s from Stoppage s where s.machine.id = ?1")
    List<Stoppage> findByMachine_Id(Long id);
//    Optional<Stoppage> findByName(@NonNull String name);
//    List<Stoppage> findAllBy(Example<Stoppage> example);

//    @Query("SELECT s FROM Stoppage s WHERE " +
//            "(:param1 IS NULL OR s.product.id = :param1) AND " +
//            "(:param2 IS NULL OR s.machine.id = :param2) AND " +
//            "(:param3 IS NULL OR s.baseTypeStoppage.id = :param3) AND " +
//            "(:param4 IS NULL OR s.subTypeStoppage.id = :param4) AND " +
//            "(:startDate IS NULL OR s.startDate >= :startDate) AND " +
//            "(:endDate IS NULL OR s.endDate <= :endDate)")
//    List<Stoppage> findEntitiesByDynamicCriteria(
//            @Param("param1") long param1,
//            @Param("param2") long param2,
//            @Param("param3") long param3,
//            @Param("param4") long param4,
//            @Param("startDate") LocalDate startDate,
//            @Param("endDate") LocalDate endDate);
}
