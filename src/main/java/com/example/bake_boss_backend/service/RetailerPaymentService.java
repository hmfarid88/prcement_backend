package com.example.bake_boss_backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.entity.EmployeePayment;
import com.example.bake_boss_backend.entity.RetailerCommission;
import com.example.bake_boss_backend.entity.RetailerPayment;
import com.example.bake_boss_backend.repository.EmployeePaymentRepository;
import com.example.bake_boss_backend.repository.RetailerCommissionRepository;
import com.example.bake_boss_backend.repository.RetailerPaymentRepository;

@Service
public class RetailerPaymentService {
    @Autowired
    private RetailerPaymentRepository retailerPaymentRepository;

    @Autowired
    private EmployeePaymentRepository employeePaymentRepository;

    @Autowired
    private RetailerCommissionRepository retailerCommissionRepository;

    public List<RetailerPayment> getRetailerPayForCurrentMonth(String username) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return retailerPaymentRepository.findRetailerPayByMonth(year, month, username);
    }

    public List<RetailerPayment> getDatewiseRetailerPay(String username, LocalDate startDate, LocalDate endDate) {
        return retailerPaymentRepository.findDatewiseRetailerPaymentByUsername(username, startDate, endDate);
    }

    public List<EmployeePayment> getEmployeePayForCurrentMonth(String username) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return employeePaymentRepository.findByMonthYearAndUsername(year, month, username);
    }

    public List<EmployeePayment> getDatewiseEmployeePay(String username, LocalDate startDate, LocalDate endDate) {
        return employeePaymentRepository.findDatewiseEmployeePayment(username, startDate, endDate);
    }

    public List<RetailerCommission> getRetailerCommissionForCurrentMonth(String username) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return retailerCommissionRepository.findRetailerCommissionByMonth(year, month, username);
    }

    public List<RetailerCommission> getDatewiseRetailerCommission(String username, LocalDate startDate, LocalDate endDate) {
        return retailerCommissionRepository.findRetailerCommissionByDate(username, startDate, endDate);
    }

    public RetailerPayment getRetailerPaymentById(Long id) {
        return retailerPaymentRepository.findById(id).orElse(null);
    }

    public Optional<RetailerCommission> getRetailerCommissionById(Long id) {
        return retailerCommissionRepository.findById(id);
    }

     public RetailerPayment updateRetailerPayInfo(Long id, RetailerPayment updatedRetailerPayment) {
        Optional<RetailerPayment> existingRetailerOpt = retailerPaymentRepository.findById(id);
    
        if (existingRetailerOpt.isPresent()) {
            RetailerPayment existingRetailer = existingRetailerOpt.get();
        
            existingRetailer.setDate(updatedRetailerPayment.getDate());
            existingRetailer.setRetailerName(updatedRetailerPayment.getRetailerName());
            existingRetailer.setNote(updatedRetailerPayment.getNote());
            existingRetailer.setAmount(updatedRetailerPayment.getAmount());
              
            return retailerPaymentRepository.save(existingRetailer);
        } else {
            throw new RuntimeException("Retailer not found with ID: " + id);
        }
    }

     public RetailerCommission updateRetailerCommission(Long id, RetailerCommission updatedRetailerPayment) {
        Optional<RetailerCommission> existingRetailerOpt = retailerCommissionRepository.findById(id);
    
        if (existingRetailerOpt.isPresent()) {
            RetailerCommission existingRetailer = existingRetailerOpt.get();
        
            existingRetailer.setDate(updatedRetailerPayment.getDate());
            existingRetailer.setRetailerName(updatedRetailerPayment.getRetailerName());
            existingRetailer.setYear(updatedRetailerPayment.getYear());
            existingRetailer.setMonth(updatedRetailerPayment.getMonth());
            existingRetailer.setNote(updatedRetailerPayment.getNote());
            existingRetailer.setAmount(updatedRetailerPayment.getAmount());
              
            return retailerCommissionRepository.save(existingRetailer);
        } else {
            throw new RuntimeException("Retailer not found with ID: " + id);
        }
    }
}
