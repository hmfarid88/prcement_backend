package com.example.bake_boss_backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bake_boss_backend.dto.SupplierBalanceDTO;
import com.example.bake_boss_backend.dto.SupplierDetailsDTO;
import com.example.bake_boss_backend.dto.TransportBalanceDTO;
import com.example.bake_boss_backend.dto.TransportDetailsDTO;
import com.example.bake_boss_backend.service.SupplierBalanceService;

@RestController
@RequestMapping("/supplierBalance")
public class SupplierBalanceController {
     @Autowired
    private SupplierBalanceService supplierBalanceService;

    @GetMapping("/supplier/balance")
    public List<SupplierBalanceDTO> getSuppliersBalanceByUsername(@RequestParam String username) {
        return supplierBalanceService.calculateSuppliersBalanceByUsername(username);
    }

      @GetMapping("/supplier-details")
    public List<SupplierDetailsDTO> getDatewiseDetailsBySupplierAndUsername(@RequestParam String username, @RequestParam String supplierName){
        return supplierBalanceService.getDetailsBySupplierAndUsername(username, supplierName);
    }

      @GetMapping("/datewise-supplier-details")
    public List<SupplierDetailsDTO> getDatewiseSupplierDetails(@RequestParam String username, @RequestParam String supplierName, LocalDate startDate, LocalDate endDate){
        return supplierBalanceService.getDatewiseDetailsBySupplierAndUsername(username, supplierName, startDate, endDate);
    }

      @GetMapping("/transport/balance")
    public List<TransportBalanceDTO> getTransportBalanceByUsername(@RequestParam String username) {
        return supplierBalanceService.calculateTransportBalanceByUsername(username);
    }

    
    @GetMapping("/transport-details")
    public List<TransportDetailsDTO> getDatewiseDetailsByTransportAndUsername(@RequestParam String username, @RequestParam String transport){
        return supplierBalanceService.getDetailsByTransportAndUsername(username, transport);
    }

     @GetMapping("/supplierDue")
    public ResponseEntity<Double> getSupplierDue() {
        Double due = supplierBalanceService.getSupplierDue();
        return ResponseEntity.ok(due);
    }
}
