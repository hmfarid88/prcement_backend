package com.example.bake_boss_backend.entity;

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
@Table(name = "employee_info", indexes = {
        @Index(name = "idx_employee_name", columnList = "employeeName")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String employeeName;
    private String fatherName;
    private String address;
    private String phoneNumber;
}
