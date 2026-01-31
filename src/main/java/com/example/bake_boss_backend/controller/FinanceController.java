package com.example.bake_boss_backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bake_boss_backend.dto.DetailsPayReceiveDTO;
import com.example.bake_boss_backend.dto.FinanceBalanceDTO;
import com.example.bake_boss_backend.dto.ProfitReportDTO;
import com.example.bake_boss_backend.service.FinanceService;
import com.example.bake_boss_backend.service.OfficePaymentService;
import com.example.bake_boss_backend.service.ProfitReportService;

@RestController
@RequestMapping("/finance")
public class FinanceController {
    private final FinanceService financeService;
    private final ProfitReportService profitService;
    private final OfficePaymentService officePaymentService;
   
    public FinanceController(FinanceService financeService, ProfitReportService profitService, OfficePaymentService officePaymentService) {
        this.financeService = financeService;
        this.profitService = profitService;
        this.officePaymentService=officePaymentService;
    }

    @GetMapping("/balance")
    public List<FinanceBalanceDTO> getFinanceBalance() {
        return financeService.getFinanceBalance();
    }

    @GetMapping("/balance-details")
    public List<DetailsPayReceiveDTO> getBalanceDetails(@RequestParam String username, String name) {
        return financeService.detailsPayReceive(username, name);
    }

    @GetMapping("/monthly-profit")
    public ProfitReportDTO monthlyProfitReport(@RequestParam String username) {
        return profitService.getMonthlyProfitReport(username);
    }

    @GetMapping("/datewise-profit")
    public ProfitReportDTO profitReport(
            @RequestParam String username,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        return profitService.getProfitReport(username, from, to);
    }

   
    @GetMapping("/opening-cash")
    public ResponseEntity<Double> getOpeningTotal() {
        Double total = officePaymentService.getOpeningCashTotal();
        return ResponseEntity.ok(total);
    }
    @GetMapping("/opening-capital")
    public ResponseEntity<Double> getOpeningCapital() {
        Double total = officePaymentService.getOpeningCapitalTotal();
        return ResponseEntity.ok(total);
    }
    @GetMapping("/opening-stock")
    public ResponseEntity<Double> getOpeningStockvalu() {
        Double total = officePaymentService.getOpeningStockTotal();
        return ResponseEntity.ok(total);
    }
}
