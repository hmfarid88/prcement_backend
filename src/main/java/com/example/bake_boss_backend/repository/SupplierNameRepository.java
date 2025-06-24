package com.example.bake_boss_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bake_boss_backend.entity.SupplierName;

public interface SupplierNameRepository extends JpaRepository<SupplierName, Long> {

    boolean existsByUsernameAndSupplierName(String username, String supplierName);

    List<SupplierName> getSupplierNameByUsername(String username);

    void deleteBySupplierName(String supplierName);

}
