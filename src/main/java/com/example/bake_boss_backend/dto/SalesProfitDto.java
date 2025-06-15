package com.example.bake_boss_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesProfitDto {
    private LocalDate date;
    private String category;
    private String productName;
    private Double costPrice;
    private Double salePrice;
    private Double qty;
    private Double discount;
}
