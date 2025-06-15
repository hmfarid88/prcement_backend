package com.example.bake_boss_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bake_boss_backend.entity.ShopInfo;

public interface ShopInfoRepository extends JpaRepository<ShopInfo, Integer> {

    Optional<ShopInfo> findByUsername(String username);

}
