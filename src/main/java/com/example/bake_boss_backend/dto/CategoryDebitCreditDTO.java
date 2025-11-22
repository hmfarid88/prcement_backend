package com.example.bake_boss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDebitCreditDTO {
    private String category;
    private Double debit;
    private Double credit;
    private double openingBalance;
}
