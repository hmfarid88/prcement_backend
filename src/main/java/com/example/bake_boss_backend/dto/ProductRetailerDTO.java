package com.example.bake_boss_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRetailerDTO {
    private LocalDate date;
    private String category;
    private String saleperson;
    private String customer;
    private String note;
    private String productName;
    private String invoiceNo;
    private String transport;
    private String truckNo;
    private Double rent;
    private Double costPrice;
    private Double dpRate;
    private Double productQty;
    private Long productId;

}
