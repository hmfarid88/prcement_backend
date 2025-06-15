package com.example.bake_boss_backend.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "employee_payment",
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_employee_name", columnList = "employeeName"),
        @Index(name = "idx_date", columnList = "date"),
        @Index(name = "idx_year_month", columnList = "year, month")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;;
    private String employeeName;
    private int year;
    private int month;
    private String note;
    private Double amount;
    private String username;
}
