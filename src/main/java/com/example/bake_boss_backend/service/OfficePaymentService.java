package com.example.bake_boss_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.dto.NetSumAmountDto;
import com.example.bake_boss_backend.dto.PaymentDto;
import com.example.bake_boss_backend.entity.EmployeePayment;
import com.example.bake_boss_backend.entity.OfficePayment;
import com.example.bake_boss_backend.entity.OfficeReceive;
import com.example.bake_boss_backend.repository.EmployeePaymentRepository;
import com.example.bake_boss_backend.repository.ExpenseRepository;
import com.example.bake_boss_backend.repository.OfficePaymentRepository;
import com.example.bake_boss_backend.repository.OfficeReceiveRepository;
import com.example.bake_boss_backend.repository.RetailerPaymentRepository;
import com.example.bake_boss_backend.repository.SupplierPaymentRepository;

@Service
public class OfficePaymentService {
    @Autowired
    private RetailerPaymentRepository retailerPaymentRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private OfficePaymentRepository officePaymentRepository;

    @Autowired
    private SupplierPaymentRepository supplierPaymentRepository;

    @Autowired
    private EmployeePaymentRepository employeePaymentRepository;

    @Autowired
    private OfficeReceiveRepository officeReceiveRepository;

    public NetSumAmountDto getNetSumAmountBeforeToday(String username, LocalDate date) {
        Double netSumAmount = retailerPaymentRepository.findNetSumAmountBeforeToday(username, date);
        return new NetSumAmountDto(netSumAmount);
    }

    public List<PaymentDto> getPaymentsForToday(String username, LocalDate date) {
        List<PaymentDto> userExpense = expenseRepository.findExpenseForToday(username, date);
        List<PaymentDto> userPayments = officePaymentRepository.findPaymentsForToday(username, date);
        List<PaymentDto> supplierPayments = supplierPaymentRepository.findSupplierPaymentsForToday(username, date);
        List<PaymentDto> employeePayments = employeePaymentRepository.findEmployeePaymentsForToday(username, date);
        List<PaymentDto> combinedPayments = new ArrayList<>();
        combinedPayments.addAll(userExpense);
        combinedPayments.addAll(userPayments);
        combinedPayments.addAll(supplierPayments);
        combinedPayments.addAll(employeePayments);
        return combinedPayments;
    }

    public List<OfficePayment> getPaymentsForCurrentMonth(String username) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return officePaymentRepository.findPaymentsByMonth(year, month, username);
    }

    public List<OfficePayment> getDatewiseOfficePay(String username, LocalDate startDate, LocalDate endDate) {
        return officePaymentRepository.findPaymentsByDate(username, startDate, endDate);
    }

    public Optional<OfficePayment> getOfficepayById(Long id) {
        return officePaymentRepository.findById(id);
    }

    public Optional<EmployeePayment> getEmployeepayById(Long id) {
        return employeePaymentRepository.findById(id);
    }

    public Optional<OfficeReceive> getOfficeReceiveById(Long id) {
        return officeReceiveRepository.findById(id);
    }

    public OfficePayment updatePaymentInfo(Long id, OfficePayment updatedExpenseInfo) {
        Optional<OfficePayment> existingExpenseOpt = officePaymentRepository.findById(id);

        if (existingExpenseOpt.isPresent()) {
            OfficePayment existingExpense = existingExpenseOpt.get();

            existingExpense.setDate(updatedExpenseInfo.getDate());
            existingExpense.setPaymentName(updatedExpenseInfo.getPaymentName());
            existingExpense.setPaymentNote(updatedExpenseInfo.getPaymentNote());
            existingExpense.setAmount(updatedExpenseInfo.getAmount());

            return officePaymentRepository.save(existingExpense);
        } else {
            throw new RuntimeException("Payment not found with ID: " + id);
        }
    }
    public OfficeReceive updateReceiveInfo(Long id, OfficeReceive updatedExpenseInfo) {
        Optional<OfficeReceive> existingExpenseOpt = officeReceiveRepository.findById(id);

        if (existingExpenseOpt.isPresent()) {
            OfficeReceive existingExpense = existingExpenseOpt.get();

            existingExpense.setDate(updatedExpenseInfo.getDate());
            existingExpense.setReceiveName(updatedExpenseInfo.getReceiveName());
            existingExpense.setReceiveNote(updatedExpenseInfo.getReceiveNote());
            existingExpense.setAmount(updatedExpenseInfo.getAmount());

            return officeReceiveRepository.save(existingExpense);
        } else {
            throw new RuntimeException("Payment not found with ID: " + id);
        }
    }
    public EmployeePayment updateEmployeePaymentInfo(Long id, EmployeePayment updatedExpenseInfo) {
        Optional<EmployeePayment> existingExpenseOpt = employeePaymentRepository.findById(id);

        if (existingExpenseOpt.isPresent()) {
            EmployeePayment existingExpense = existingExpenseOpt.get();

            existingExpense.setDate(updatedExpenseInfo.getDate());
            existingExpense.setEmployeeName(updatedExpenseInfo.getEmployeeName());
            existingExpense.setYear(updatedExpenseInfo.getYear());
            existingExpense.setMonth(updatedExpenseInfo.getMonth());
            existingExpense.setNote(updatedExpenseInfo.getNote());
            existingExpense.setAmount(updatedExpenseInfo.getAmount());

            return employeePaymentRepository.save(existingExpense);
        } else {
            throw new RuntimeException("Payment not found with ID: " + id);
        }
    }

}
