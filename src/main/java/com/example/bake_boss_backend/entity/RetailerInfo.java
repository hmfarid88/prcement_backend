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
@Table(name = "retailer_info", indexes = {
        @Index(name = "idx_retailer_code", columnList = "retailerCode"),
        @Index(name = "idx_retailer_name", columnList = "retailerName")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetailerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String retailerName;
    private String retailerCode;
    private String thanaName;
    private String zillaName;
    private String areaName;
    private String mobileNumber;
    private String salesPerson;
    private String status;
}
