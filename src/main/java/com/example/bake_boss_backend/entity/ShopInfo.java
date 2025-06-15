package com.example.bake_boss_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "shop_info",
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_phone_number", columnList = "phoneNumber"),
        @Index(name = "idx_shop_name", columnList = "shopName")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String shopName;
    private String phoneNumber;
    private String address;
    private String email;
    private String username;
}
