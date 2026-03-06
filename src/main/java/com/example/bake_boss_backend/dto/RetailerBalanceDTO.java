package com.example.bake_boss_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RetailerBalanceDTO {
    private String category;
    private String areaName;
    private String retailerName;
    private String retailerCode;
    private String salesPerson;
    private Double totalProductQty;
    private Double totalProductValue;
    private Double totalPayment;
    private Double totalCommission;
    private Double debit;
    private Double credit;

    public RetailerBalanceDTO(String category, String areaName, String retailerName,
                              String retailerCode, String salesPerson,
                              Double totalProductQty, Double totalProductValue,
                              Double totalPayment, Double totalCommission) {

        this.category = category;
        this.areaName = areaName;
        this.retailerName = retailerName;
        this.retailerCode = retailerCode;
        this.salesPerson = salesPerson;
        this.totalProductQty = totalProductQty;
        this.totalProductValue = totalProductValue;
        this.totalPayment = totalPayment;
        this.totalCommission = totalCommission;
    }

}
