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
    name = "supplier_commission",
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_username_date", columnList = "username, date"),
        @Index(name = "idx_supplier_name", columnList = "supplierName"),
        @Index(name = "idx_year_month_username", columnList = "year, month, username")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCommission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;;
    private String supplierName;
    private int year;
    private int month;
    private String note;
    private Double amount;
    private String username;
}
