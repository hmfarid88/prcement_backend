package com.example.bake_boss_backend.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TransferStockRequest {
    private LocalDate date;

    private String sourceWarehouse;
    private String sourceCategory;

    private String productName;
    private Double transferQty;

    private String destinationWarehouse;
    private String destinationCategory;

    private String username;
    private String note;
}
