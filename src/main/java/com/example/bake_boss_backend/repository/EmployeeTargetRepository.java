package com.example.bake_boss_backend.repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bake_boss_backend.entity.EmployeeTarget;

public interface EmployeeTargetRepository extends JpaRepository<EmployeeTarget, Long>{
  
    Optional<EmployeeTarget> findByEmployeeNameAndYearAndMonth(String employeeName, int year, int month);

    List<EmployeeTarget> findByEmployeeName(String employeeName);
        
}
