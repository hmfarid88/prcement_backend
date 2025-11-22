package com.example.bake_boss_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.dto.SupplierBalanceDTO;
import com.example.bake_boss_backend.dto.SupplierDetailsDTO;
import com.example.bake_boss_backend.dto.TransportBalanceDTO;
import com.example.bake_boss_backend.dto.TransportDetailsDTO;
import com.example.bake_boss_backend.repository.ProductStockrepository;
import com.example.bake_boss_backend.repository.SupplierCommissionRepository;
import com.example.bake_boss_backend.repository.SupplierPaymentRepository;
import com.example.bake_boss_backend.repository.TransportPaymentRepository;

@Service
public class SupplierBalanceService {
    @Autowired
    private ProductStockrepository productStockRepository;

    @Autowired
    private SupplierPaymentRepository supplierPaymentRepository;

    @Autowired
    private SupplierCommissionRepository supplierCommissionRepository;

    @Autowired
    private TransportPaymentRepository transportPaymentRepository;

    public List<SupplierBalanceDTO> calculateSuppliersBalanceByUsername(String username) {
        List<Object[]> materialCosts = productStockRepository
                .findTotalProductCostGroupedBySupplierAndUsername(username);

        List<Object[]> payments = supplierPaymentRepository.findTotalPaymentGroupedBySupplierAndUsername(username);

        List<Object[]> commission = supplierCommissionRepository
                .findTotalCommissionGroupedBySupplierAndUsername(username);

        Map<String, Double> balanceMap = new HashMap<>();

        // Calculate the total material costs by supplierName
        for (Object[] row : materialCosts) {
            String supplierName = (String) row[0];
            Double totalMaterialCost = (Double) row[1];
            balanceMap.put(supplierName, totalMaterialCost);
        }

        // Subtract the total payments by supplierName
        for (Object[] row : payments) {
            String supplierName = (String) row[0];
            Double totalPayment = (Double) row[1];
            balanceMap.put(supplierName, balanceMap.getOrDefault(supplierName, 0.0) - totalPayment);
        }

        for (Object[] row : commission) {
            String supplierName = (String) row[0];
            Double totalCommission = (Double) row[1];
            balanceMap.put(supplierName, balanceMap.getOrDefault(supplierName, 0.0) - totalCommission);
        }

        // Convert the map to a list of DTOs
        return balanceMap.entrySet().stream()
                .map(entry -> new SupplierBalanceDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<TransportBalanceDTO> calculateTransportBalanceByUsername(String username) {
        List<Object[]> rentAmount = productStockRepository.findTotalRentGroupedByTransportAndUsername(username);

        List<Object[]> payments = transportPaymentRepository.findTotalPaymentGroupedByTransportAndUsername(username);

        Map<String, Double> balanceMap = new HashMap<>();

        // Calculate the total material costs by supplierName
        for (Object[] row : rentAmount) {
            String transport = (String) row[0];
            Double totalRent = (Double) row[1];
            balanceMap.put(transport, totalRent);
        }

        // Subtract the total payments by supplierName
        for (Object[] row : payments) {
            String transport = (String) row[0];
            Double totalPayment = (Double) row[1];
            balanceMap.put(transport, balanceMap.getOrDefault(transport, 0.0) - totalPayment);
        }

        // Convert the map to a list of DTOs
        return balanceMap.entrySet().stream()
                .map(entry -> new TransportBalanceDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<SupplierDetailsDTO> getDetailsBySupplierAndUsername(String username, String supplierName) {
        List<SupplierDetailsDTO> productValue = Optional.ofNullable(
                productStockRepository.findProductDetailsByUsernameAndSupplierName(username, supplierName))
                .orElse(Collections.emptyList());

        List<SupplierDetailsDTO> paymentValue = Optional.ofNullable(
                supplierPaymentRepository.findPaymentDetailsByUsernameAndSupplierName(username, supplierName))
                .orElse(Collections.emptyList());

        List<SupplierDetailsDTO> commissionValue = Optional.ofNullable(
                supplierCommissionRepository.findCommissionDetailsByUsernameAndSupplierName(username, supplierName))
                .orElse(Collections.emptyList());

        List<SupplierDetailsDTO> combinedDetails = new ArrayList<>();
        combinedDetails.addAll(productValue);
        combinedDetails.addAll(paymentValue);
        combinedDetails.addAll(commissionValue);

        combinedDetails.sort(
                Comparator.comparing(SupplierDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

        return combinedDetails;
    }

    public List<SupplierDetailsDTO> getDatewiseDetailsBySupplierAndUsername(String username, String supplierName, LocalDate startDate, LocalDate endDate) {
        List<SupplierDetailsDTO> productValue = Optional.ofNullable(
                productStockRepository.findDatewiseProductDetailsByUsernameAndSupplierName(username, supplierName, startDate, endDate))
                .orElse(Collections.emptyList());

        List<SupplierDetailsDTO> paymentValue = Optional.ofNullable(
                supplierPaymentRepository.findDatewisePaymentDetailsByUsernameAndSupplierName(username, supplierName, startDate, endDate))
                .orElse(Collections.emptyList());

        List<SupplierDetailsDTO> commissionValue = Optional.ofNullable(
                supplierCommissionRepository.findDatewiseCommissionDetailsByUsernameAndSupplierName(username, supplierName,startDate, endDate))
                .orElse(Collections.emptyList());

        List<SupplierDetailsDTO> combinedDetails = new ArrayList<>();
        combinedDetails.addAll(productValue);
        combinedDetails.addAll(paymentValue);
        combinedDetails.addAll(commissionValue);

        combinedDetails.sort(
                Comparator.comparing(SupplierDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

        return combinedDetails;
    }

    public List<TransportDetailsDTO> getDetailsByTransportAndUsername(String username, String transport) {
        List<TransportDetailsDTO> rentValue = Optional.ofNullable(
                productStockRepository.findRentDetailsByUsernameAndSupplierName(username, transport))
                .orElse(Collections.emptyList());

        List<TransportDetailsDTO> paymentValue = Optional.ofNullable(
                transportPaymentRepository.findPaymentDetailsByUsernameAndTransport(username, transport))
                .orElse(Collections.emptyList());

        List<TransportDetailsDTO> combinedDetails = new ArrayList<>();
        combinedDetails.addAll(rentValue);
        combinedDetails.addAll(paymentValue);
        
        combinedDetails.sort(
                Comparator.comparing(TransportDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

        return combinedDetails;
    }
}
