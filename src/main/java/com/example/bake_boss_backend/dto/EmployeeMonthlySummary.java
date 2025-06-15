package com.example.bake_boss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeMonthlySummary {
    private String employeeName;
    private int year;
    private int month;
    private Double targetAmount;
    private Double productQty;
    private Double soldValue;
    private Double paymentValue;
}
