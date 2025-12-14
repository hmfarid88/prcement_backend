package com.example.bake_boss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyWarehouseStockDTO {
    private String warehouse;
    private String productName;
    private Double previousQty;
    private Double todayEntryQty;
    private Double todaySaleQty;
    private Double presentQty;
    private Double costPrice;
    private Double totalValue;
}
