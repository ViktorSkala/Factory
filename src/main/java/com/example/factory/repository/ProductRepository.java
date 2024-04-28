package com.example.factory.repository;

import com.example.factory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository <Product, Long> {
    @Query("select p from Product p where p.name = ?1 and p.numbersInPack = ?2 and p.machine.id = ?3")
    Optional<Product> findByNameAndNumbersInPackAndMachine_Id(String name, Integer numbersInPack, Long id);
    List<Product> findProductByMachine_IdOrderByNameAsc(Long id);
    Optional<Product> findByNameAndNumbersInPack(@NonNull String name, @NonNull Integer numbersInPack);

}
