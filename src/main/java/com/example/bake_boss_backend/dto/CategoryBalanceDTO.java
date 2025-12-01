package com.example.bake_boss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryBalanceDTO {
    private String category;
    private Double totalProductQty;
    private Double totalProductValue;
    private Double totalPayment;
    private Double totalCommission;
}
