package com.example.bake_boss_backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceBalanceDTO {
    private String name;
    private Double totalPayment;
    private Double totalReceive;
    private Double balance;
}
