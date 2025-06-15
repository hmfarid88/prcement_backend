package com.example.bake_boss_backend.entity;

import java.time.LocalDate;

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
    name = "order_info",
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_date", columnList = "date"),
        @Index(name = "idx_product_name", columnList = "productName"),
        @Index(name = "idx_username_date", columnList = "username, date"),
        @Index(name = "idx_username_product", columnList = "username, productName")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private LocalDate date;
    private String retailer;
    private String orderNote;
    private String productName;
    private Double saleRate;
    private Double orderQty;
    private Double deliveredQty;
    private String username;
}
