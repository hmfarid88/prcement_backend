package com.example.bake_boss_backend.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.dto.ProfitReportDTO;
import com.example.bake_boss_backend.repository.EmployeePaymentRepository;
import com.example.bake_boss_backend.repository.ExpenseRepository;
import com.example.bake_boss_backend.repository.ProductStockrepository;
import com.example.bake_boss_backend.repository.SupplierCommissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfitReportService {
    private final ProductStockrepository productStockRepo;
    private final ExpenseRepository expenseRepo;
    private final EmployeePaymentRepository employeeRepo;
    private final SupplierCommissionRepository supplierRepo;

    public ProfitReportDTO getMonthlyProfitReport(String username) {
        Double salesProfit = productStockRepo.getMonthlySalesProfit(username);
        Double rent = productStockRepo.getMonthlyTotalRent(username);
        Double expense = expenseRepo.getMonthlyTotalExpense(username);
        Double employee = employeeRepo.getMonthlyTotalEmployeePayment(username);
        Double supplierCommission = supplierRepo.getMonthlyTotalSupplierCommission(username);

        Double netProfit = salesProfit
                + supplierCommission
                - rent
                - expense
                - employee;

        return new ProfitReportDTO(
                salesProfit,
                supplierCommission,
                rent,
                expense,
                employee,
                netProfit);
    }

    public ProfitReportDTO getProfitReport(
            String username, LocalDate from, LocalDate to) {

        Double salesProfit = productStockRepo.getSalesProfit(username, from, to);
        Double rent = productStockRepo.getTotalRent(username, from, to);
        Double expense = expenseRepo.getTotalExpense(username, from, to);
        Double employee = employeeRepo.getTotalEmployeePayment(username, from, to);
        Double supplierCommission = supplierRepo.getTotalSupplierCommission(username, from, to);

        Double netProfit = salesProfit
                + supplierCommission
                - rent
                - expense
                - employee;

        return new ProfitReportDTO(
                salesProfit,
                supplierCommission,
                rent,
                expense,
                employee,
                netProfit);
    }
}
