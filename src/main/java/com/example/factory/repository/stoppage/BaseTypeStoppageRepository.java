package com.example.factory.repository.stoppage;

import com.example.factory.model.stoppage.BaseTypeStoppage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface BaseTypeStoppageRepository extends JpaRepository<BaseTypeStoppage, Long> {
    Optional<BaseTypeStoppage> findByName(@NonNull String name);

}
