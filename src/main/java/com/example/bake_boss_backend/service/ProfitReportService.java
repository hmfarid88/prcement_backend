package com.example.bake_boss_backend.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.bake_boss_backend.dto.ProfitLossAccountDTO;
import com.example.bake_boss_backend.dto.ProfitLossAccountLineDTO;
import com.example.bake_boss_backend.dto.ProfitReportDTO;
import com.example.bake_boss_backend.repository.EmployeePaymentRepository;
import com.example.bake_boss_backend.repository.ExpenseRepository;
import com.example.bake_boss_backend.repository.ProductStockrepository;
import com.example.bake_boss_backend.repository.SupplierCommissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfitReportService {
    private static final DateTimeFormatter ACCOUNT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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


public ProfitLossAccountDTO getProfitLossAccount(
        String username, LocalDate from, LocalDate to) {

    if (from == null || to == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Start date and end date are required.");
    }

    if (from.isAfter(to)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "The start date must not be after the end date.");
    }

    // Get the report for the requested period.
    ProfitReportDTO report = getProfitReport(username, from, to);

    // Normalize null amounts to zero.
    double salesProfit = safeAmount(report.getSalesProfit());
    double salary = safeAmount(report.getEmployeePayment());
    double rent = safeAmount(report.getRent());
    double otherExpenses = safeAmount(report.getExpense());
    double supplierIncentives =
            safeAmount(report.getSupplierCommission());

    // Separate gross profit and gross loss.
    double grossProfit = Math.max(salesProfit, 0.0);
    double grossLoss = Math.max(-salesProfit, 0.0);

    // Calculate total operating expenses.
    double expenses = salary + rent + otherExpenses;

    /*
     * Assumption:
     * Supplier incentives are income received from suppliers.
     *
     * Net result = Sales profit + Supplier incentives - Expenses
     */
    double netResult = salesProfit + supplierIncentives - expenses;

    // Separate net profit and net loss.
    double netProfit = Math.max(netResult, 0.0);
    double netLoss = Math.max(-netResult, 0.0);

    // Debit side.
    List<ProfitLossAccountLineDTO> debit = new ArrayList<>();

    debit.add(new ProfitLossAccountLineDTO(
            "Gross loss transferred from trading account",
            grossLoss));

    debit.add(new ProfitLossAccountLineDTO(
            "Salary",
            salary));

    debit.add(new ProfitLossAccountLineDTO(
            "Rent",
            rent));

    debit.add(new ProfitLossAccountLineDTO(
            "Other business expenses",
            otherExpenses));

    debit.add(new ProfitLossAccountLineDTO(
            "Net profit",
            netProfit));

    // Credit side.
    List<ProfitLossAccountLineDTO> credit = new ArrayList<>();

    credit.add(new ProfitLossAccountLineDTO(
            "Gross profit transferred from trading account",
            grossProfit));

    credit.add(new ProfitLossAccountLineDTO(
            "Supplier incentives / rebates",
            supplierIncentives));

    credit.add(new ProfitLossAccountLineDTO(
            "Other income",
            0.0));

    credit.add(new ProfitLossAccountLineDTO(
            "Net loss",
            netLoss));

    // Calculate account totals.
    double totalDebit = grossLoss + expenses + netProfit;
    double totalCredit = grossProfit + supplierIncentives + netLoss;

    // Validate that the debit and credit sides balance.
    if (Math.abs(totalDebit - totalCredit) > 0.01) {
        throw new IllegalStateException(
                "Profit and loss account is not balanced. "
                        + "Debit: " + totalDebit
                        + ", Credit: " + totalCredit);
    }

    return new ProfitLossAccountDTO(
            from,
            to,
            "For the period from "
                    + from.format(ACCOUNT_DATE_FORMAT)
                    + " to "
                    + to.format(ACCOUNT_DATE_FORMAT),
            debit,
            credit,
            totalDebit,
            totalCredit,
            netProfit,
            netLoss
    );
}

/**
 * Converts null monetary values to zero.
 */
private double safeAmount(Double amount) {
    return amount == null ? 0.0 : amount;
}

}
