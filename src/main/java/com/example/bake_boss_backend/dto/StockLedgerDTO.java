package com.example.bake_boss_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockLedgerDTO {
    private LocalDate date;

    private String warehouse;

    private String category;

    private String productName;

    private Double openingQty;

    private Double storedQty;

    private Double soldQty;

    private Double transferInQty;

    private Double transferOutQty;

    private Double closingQty;

    private Double costPrice;
}
