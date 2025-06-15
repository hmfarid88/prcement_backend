package com.example.bake_boss_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "product_name",
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_product_name", columnList = "productName"),
        @Index(name = "idx_username_product_name", columnList = "username, productName")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String username;
}
