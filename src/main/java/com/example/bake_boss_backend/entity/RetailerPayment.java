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
    name = "retailer_payment",
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_retailer_name", columnList = "retailerName"),
        @Index(name = "idx_username_retailer_name", columnList = "username, retailerName"),
        @Index(name = "idx_username_date", columnList = "username, date")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetailerPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String retailerName;
    private String note;
    private Double amount;
    private String username;
}
