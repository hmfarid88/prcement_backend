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
    name = "retailer_commission",
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_retailer_name", columnList = "retailerName"),
        @Index(name = "idx_year_month_username", columnList = "year, month, username"),
        @Index(name = "idx_username_retailer", columnList = "username, retailerName")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetailerCommission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;;
    private String retailerName;
    private int year;
    private int month;
    private String note;
    private Double amount;
    private String username;
}
