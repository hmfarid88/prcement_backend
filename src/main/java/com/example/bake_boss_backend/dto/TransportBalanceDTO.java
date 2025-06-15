package com.example.bake_boss_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransportBalanceDTO {
    private String transport;
    private Double balance;
}
