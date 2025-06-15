package com.example.bake_boss_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bake_boss_backend.entity.RetailerInfo;

public interface RetailerInfoRepository extends JpaRepository<RetailerInfo, Long> {

    boolean existsByRetailerName(String retailerName);

    List<RetailerInfo> findBySalesPerson(String salesPerson);

    RetailerInfo findByRetailerName(String retailerName);

    List<RetailerInfo> findAllByOrderByRetailerNameAsc();

    boolean existsByRetailerNameAndIdNot(String retailerName, Long id);
    
}
