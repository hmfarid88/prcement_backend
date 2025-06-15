package com.example.bake_boss_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bake_boss_backend.entity.PaymentName;

public interface PaymentNameRepository extends JpaRepository<PaymentName, Integer>{

    boolean existsByUsernameAndPaymentPerson(String username, String paymentPerson);

    List<PaymentName> getPaymentPersonByUsername(String username);
    
}
