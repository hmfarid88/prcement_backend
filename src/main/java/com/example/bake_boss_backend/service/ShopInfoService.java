package com.example.bake_boss_backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.entity.ShopInfo;
import com.example.bake_boss_backend.repository.ShopInfoRepository;

@Service
public class ShopInfoService {
    private final ShopInfoRepository shopInfoRepository;

    @Autowired
    public ShopInfoService(ShopInfoRepository shopInfoRepository) {
        this.shopInfoRepository = shopInfoRepository;
    }

    public ShopInfo saveOrUpdateShopInfo(ShopInfo shopInfo) {
        Optional<ShopInfo> existingShopInfo = shopInfoRepository.findByUsername(shopInfo.getUsername());

        if (existingShopInfo.isPresent()) {
            // Update existing shop info
            ShopInfo currentShopInfo = existingShopInfo.get();
            currentShopInfo.setShopName(shopInfo.getShopName());
            currentShopInfo.setPhoneNumber(shopInfo.getPhoneNumber());
            currentShopInfo.setAddress(shopInfo.getAddress());
            currentShopInfo.setEmail(shopInfo.getEmail());
            return shopInfoRepository.save(currentShopInfo);
        } else {
            // Save new shop info
            return shopInfoRepository.save(shopInfo);
        }
    }

    public Optional<ShopInfo> getShopInfoByUsername(String username) {
        return shopInfoRepository.findByUsername(username);
    }
}
