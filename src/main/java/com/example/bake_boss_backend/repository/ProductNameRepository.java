package com.example.bake_boss_backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bake_boss_backend.entity.ProductName;

public interface ProductNameRepository extends JpaRepository<ProductName, Long>{

    boolean existsByUsernameAndProductName(String username, String productName);
    
}
