package com.example.bake_boss_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.entity.TransportPayment;
import com.example.bake_boss_backend.repository.TransportPaymentRepository;

@Service
public class TransportPaymentService {
    @Autowired
    private TransportPaymentRepository transportPaymentRepository;

     public List<TransportPayment> getTransportPayForCurrentMonth(String username) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return transportPaymentRepository.findPaymentsByMonth(year, month, username);
    }

    public List<TransportPayment> getDatewiseTransportPayment(String username, LocalDate startDate, LocalDate endDate) {
        return transportPaymentRepository.findPaymentsByDate(username, startDate, endDate);
    }

     public Optional<TransportPayment> getTransportPayById(Long id) {
        return transportPaymentRepository.findById(id);
    }
public TransportPayment updateTransportPaymentInfo(Long id, TransportPayment updatedExpenseInfo) {
        Optional<TransportPayment> existingExpenseOpt = transportPaymentRepository.findById(id);

        if (existingExpenseOpt.isPresent()) {
            TransportPayment existingExpense = existingExpenseOpt.get();

            existingExpense.setDate(updatedExpenseInfo.getDate());
            existingExpense.setTransport(updatedExpenseInfo.getTransport());
            existingExpense.setNote(updatedExpenseInfo.getNote());
            existingExpense.setAmount(updatedExpenseInfo.getAmount());

            return transportPaymentRepository.save(existingExpense);
        } else {
            throw new RuntimeException("Payment not found with ID: " + id);
        }
    }
}
