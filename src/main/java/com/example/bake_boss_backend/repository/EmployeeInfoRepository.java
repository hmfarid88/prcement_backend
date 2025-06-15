package com.example.bake_boss_backend.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bake_boss_backend.entity.EmployeeInfo;

public interface EmployeeInfoRepository extends JpaRepository<EmployeeInfo, Long>{

    boolean existsByEmployeeName(String employeeName);

    List<EmployeeInfo> findAllByOrderByEmployeeNameAsc();

}
