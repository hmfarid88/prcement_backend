package com.example.bake_boss_backend.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bake_boss_backend.entity.ShopInfo;
import com.example.bake_boss_backend.service.ShopInfoService;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
      private final ShopInfoService shopInfoService;
    

    @Autowired
    public InvoiceController(
            ShopInfoService shopInfoService) {
            this.shopInfoService = shopInfoService;
    }
  
    @PutMapping("/addShopInfo")
    public ShopInfo saveOrUpdateShopInfo(@RequestBody ShopInfo shopInfo) {
        return shopInfoService.saveOrUpdateShopInfo(shopInfo);
    }

    @GetMapping("/getShopInfo")
    public Optional<ShopInfo> getShopInfo(@RequestParam String username) {
        return shopInfoService.getShopInfoByUsername(username);
    }
}
