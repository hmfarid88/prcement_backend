package com.example.bake_boss_backend.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "transport", indexes = {
        @Index(name = "idx_transport", columnList = "transport"),
        @Index(name = "idx_username_transport", columnList = "username, transport")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transport {
 @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String transport;
    private String username;
}
