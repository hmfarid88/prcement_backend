package com.example.bake_boss_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveDto {
    private LocalDate date;
    private String name;
    private String note;
    private Double amount;
}
