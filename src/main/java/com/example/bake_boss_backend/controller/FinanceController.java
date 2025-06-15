package com.example.bake_boss_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bake_boss_backend.dto.DetailsPayReceiveDTO;
import com.example.bake_boss_backend.dto.FinanceBalanceDTO;
import com.example.bake_boss_backend.service.FinanceService;

@RestController
@RequestMapping("/finance")
public class FinanceController {
    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/balance")
    public List<FinanceBalanceDTO> getFinanceBalance() {
        return financeService.getFinanceBalance();
    }

     @GetMapping("/balance-details")
    public List<DetailsPayReceiveDTO> getBalanceDetails(@RequestParam String username, String name) {
        return financeService.detailsPayReceive(username, name);
    }
}
