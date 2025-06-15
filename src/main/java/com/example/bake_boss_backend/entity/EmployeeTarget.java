package com.example.bake_boss_backend.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "employee_target",
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_employee_name", columnList = "employeeName"),
        @Index(name = "idx_username_year_month", columnList = "username, year, month"),
        @Index(name = "idx_employee_year_month", columnList = "employeeName, year, month")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTarget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;;
    private String employeeName;
    private String targetName;
    private int year;
    private int month;
    private Double amount;
    private String username;
}
