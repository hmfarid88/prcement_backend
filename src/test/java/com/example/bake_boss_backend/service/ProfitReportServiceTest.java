package com.example.bake_boss_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.bake_boss_backend.dto.ProfitLossAccountDTO;
import com.example.bake_boss_backend.repository.EmployeePaymentRepository;
import com.example.bake_boss_backend.repository.ExpenseRepository;
import com.example.bake_boss_backend.repository.ProductStockrepository;
import com.example.bake_boss_backend.repository.SupplierCommissionRepository;

class ProfitReportServiceTest {
    private final ProductStockrepository productStockRepo = mock(ProductStockrepository.class);
    private final ExpenseRepository expenseRepo = mock(ExpenseRepository.class);
    private final EmployeePaymentRepository employeeRepo = mock(EmployeePaymentRepository.class);
    private final SupplierCommissionRepository supplierRepo = mock(SupplierCommissionRepository.class);
    private final ProfitReportService service = new ProfitReportService(
            productStockRepo, expenseRepo, employeeRepo, supplierRepo);

    private final LocalDate from = LocalDate.of(2026, 1, 1);
    private final LocalDate to = LocalDate.of(2026, 1, 31);

    @BeforeEach
    void setUp() {
        when(productStockRepo.getSalesProfit("owner", from, to)).thenReturn(100.0);
        when(productStockRepo.getTotalRent("owner", from, to)).thenReturn(10.0);
        when(expenseRepo.getTotalExpense("owner", from, to)).thenReturn(20.0);
        when(employeeRepo.getTotalEmployeePayment("owner", from, to)).thenReturn(30.0);
        when(supplierRepo.getTotalSupplierCommission("owner", from, to)).thenReturn(5.0);
    }

    @Test
    void buildsBalancedAccountWithNetProfit() {
        ProfitLossAccountDTO account = service.getProfitLossAccount("owner", from, to);

        assertThat(account.getPeriod()).isEqualTo("For the period from 01/01/2026 to 31/01/2026");
        assertThat(account.getDebit()).extracting("description", "amount").contains(
                org.assertj.core.groups.Tuple.tuple("Salary", 30.0),
                org.assertj.core.groups.Tuple.tuple("Rent", 10.0),
                org.assertj.core.groups.Tuple.tuple("Net profit", 45.0));
        assertThat(account.getCredit()).extracting("description", "amount").contains(
                org.assertj.core.groups.Tuple.tuple("Gross profit brought forward", 100.0),
                org.assertj.core.groups.Tuple.tuple("Supplier incentives / rebates", 5.0));
        assertThat(account.getTotalDebit()).isEqualTo(105.0);
        assertThat(account.getTotalCredit()).isEqualTo(105.0);
        assertThat(account.getNetProfit()).isEqualTo(45.0);
        assertThat(account.getNetLoss()).isZero();
    }

    @Test
    void putsGrossLossAndBalancingNetLossOnOppositeSides() {
        when(productStockRepo.getSalesProfit("owner", from, to)).thenReturn(-100.0);
        when(productStockRepo.getTotalRent("owner", from, to)).thenReturn(10.0);
        when(expenseRepo.getTotalExpense("owner", from, to)).thenReturn(20.0);
        when(employeeRepo.getTotalEmployeePayment("owner", from, to)).thenReturn(30.0);
        when(supplierRepo.getTotalSupplierCommission("owner", from, to)).thenReturn(5.0);

        ProfitLossAccountDTO account = service.getProfitLossAccount("owner", from, to);

        assertThat(account.getDebit()).extracting("description", "amount").contains(
                org.assertj.core.groups.Tuple.tuple("Gross loss brought forward", 100.0));
        assertThat(account.getCredit()).extracting("description", "amount").contains(
                org.assertj.core.groups.Tuple.tuple("Net loss", 155.0));
        assertThat(account.getTotalDebit()).isEqualTo(160.0);
        assertThat(account.getTotalCredit()).isEqualTo(160.0);
        assertThat(account.getNetProfit()).isZero();
        assertThat(account.getNetLoss()).isEqualTo(155.0);
    }

    @Test
    void rejectsAnInvalidDateRange() {
        assertThatThrownBy(() -> service.getProfitLossAccount("owner", to, from))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(exception.getReason())
                            .isEqualTo("The start date must not be after the end date.");
                });
    }
}
