package com.example.bake_boss_backend.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Index;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity
@Table(
    name = "transport_payment",
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_transport", columnList = "transport"),
        @Index(name = "idx_username_transport", columnList = "username, transport"),
        @Index(name = "idx_username_date", columnList = "username, date")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String transport;
    private String note;
    private Double amount;
    private String username;
}
