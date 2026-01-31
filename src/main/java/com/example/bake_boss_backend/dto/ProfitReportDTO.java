package com.example.bake_boss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfitReportDTO {
    private Double salesProfit;
    private Double supplierCommission;
    private Double rent;
    private Double expense;
    private Double employeePayment;
    private Double netProfit;

}
