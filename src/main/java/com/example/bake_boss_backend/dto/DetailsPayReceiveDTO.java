package com.example.bake_boss_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DetailsPayReceiveDTO {
    private LocalDate date;
    private String paymentNote;
    private Double payment;
    private Double receive;
}
