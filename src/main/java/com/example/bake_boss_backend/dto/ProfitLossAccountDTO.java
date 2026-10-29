package com.example.bake_boss_backend.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfitLossAccountDTO {
    private LocalDate fromDate;
    private LocalDate toDate;
    private String period;
    private List<ProfitLossAccountLineDTO> debit;
    private List<ProfitLossAccountLineDTO> credit;
    private Double totalDebit;
    private Double totalCredit;
    private Double netProfit;
    private Double netLoss;
}
