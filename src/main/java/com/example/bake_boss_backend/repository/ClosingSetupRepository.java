package com.example.bake_boss_backend.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.bake_boss_backend.entity.ClosingSetup;

public interface ClosingSetupRepository extends JpaRepository<ClosingSetup, Integer> {
    Optional<ClosingSetup> findByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);

    @Query("SELECT c FROM ClosingSetup c WHERE c.id = (SELECT MAX(cs.id) FROM ClosingSetup cs)")
    ClosingSetup findLastClosingSetup();
}
