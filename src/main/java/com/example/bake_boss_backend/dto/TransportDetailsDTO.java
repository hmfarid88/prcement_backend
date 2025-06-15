package com.example.bake_boss_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransportDetailsDTO {
    private LocalDate date;
    private String truckno;
    private Double productQty;
    private Double rent;
    private Double payment;

}
