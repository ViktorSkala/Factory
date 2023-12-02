package com.example.factory.repositoty;

import com.example.factory.model.ProductivityInHour;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductivityInHourRepository extends JpaRepository<ProductivityInHour, Long> {
}