package com.example.bake_boss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetailerBalanceDTO {
    private String retailerName;
    private String retailerCode;
    private String salesPerson;
    private Double totalProductQty;
    private Double totalProductValue;
    private Double totalPayment;
    private Double totalCommission;
  
}
