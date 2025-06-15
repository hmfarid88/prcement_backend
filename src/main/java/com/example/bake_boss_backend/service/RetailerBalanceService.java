package com.example.bake_boss_backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.dto.RetailerBalanceDTO;
import com.example.bake_boss_backend.dto.RetailerDetailsDTO;
import com.example.bake_boss_backend.entity.ClosingSetup;
import com.example.bake_boss_backend.entity.RetailerInfo;
import com.example.bake_boss_backend.repository.ClosingSetupRepository;
import com.example.bake_boss_backend.repository.ProductStockrepository;
import com.example.bake_boss_backend.repository.RetailerCommissionRepository;
import com.example.bake_boss_backend.repository.RetailerInfoRepository;
import com.example.bake_boss_backend.repository.RetailerPaymentRepository;

@Service
public class RetailerBalanceService {
    @Autowired
    private RetailerPaymentRepository retailerPaymentRepository;

    @Autowired
    private ClosingSetupRepository closingSetupRepository;

    @Autowired
    private RetailerInfoRepository retailerInfoRepository;
    
    @Autowired
    private ProductStockrepository productStockrepository;

    @Autowired
    private RetailerCommissionRepository retailerCommissionRepository;


    public List<RetailerBalanceDTO> retailerBalance() {
        ClosingSetup lastClosingSetup = closingSetupRepository.findLastClosingSetup();

        if (lastClosingSetup != null) {
            LocalDate startDate = lastClosingSetup.getStartDate();
            LocalDate endDate = lastClosingSetup.getEndDate();
            return retailerPaymentRepository.findRetailerBalanceBetweenDates(startDate, endDate);
        }
        return List.of();

    }

    public List<RetailerBalanceDTO> datewiseRetailerBalance(LocalDate startDate, LocalDate endDate) {
       return retailerPaymentRepository.findRetailerBalanceBetweenDates(startDate, endDate);

    }

    public List<RetailerBalanceDTO> salesRetailerBalance(String salesPerson) {
        ClosingSetup lastClosingSetup = closingSetupRepository.findLastClosingSetup();

        if (lastClosingSetup != null) {
            LocalDate startDate = lastClosingSetup.getStartDate();
            LocalDate endDate = lastClosingSetup.getEndDate();

            return retailerPaymentRepository.findSalesRetailerBalanceBetweenDates(salesPerson, startDate, endDate);
        }
        // Return an empty list if no ClosingSetup is found
        return List.of();

    }

    public List<RetailerBalanceDTO> salesDatewiseRetailerBalance(String salesPerson, LocalDate startDate, LocalDate endDate) {
        return retailerPaymentRepository.findSalesRetailerBalanceBetweenDates(salesPerson, startDate, endDate);
    }

    public List<RetailerDetailsDTO> getDatewiseDetailsByRetailerAndUsername(String retailerName, String username) {
        ClosingSetup lastClosingSetup = closingSetupRepository.findLastClosingSetup();
    
        if (lastClosingSetup != null) {
            LocalDate startDate = lastClosingSetup.getStartDate();
            LocalDate endDate = lastClosingSetup.getEndDate();
    
            List<RetailerDetailsDTO> productValue = Optional.ofNullable(
                productStockrepository.findProductDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate))
                .orElse(Collections.emptyList());
    
            List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
                retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate))
                .orElse(Collections.emptyList());
    
            List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
                retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerName(username, retailerName,  startDate, endDate))
                .orElse(Collections.emptyList());
    
            List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
            combinedDetails.addAll(productValue);
            combinedDetails.addAll(paymentValue);
            combinedDetails.addAll(commissionValue);
    
            combinedDetails.sort(Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));
    
            return combinedDetails;
        }
    
        return Collections.emptyList();
    }
    

    public List<RetailerDetailsDTO> getDatewiseRetailerDetailsByRetailerAndUsername(
        String retailerName, String username, LocalDate startDate, LocalDate endDate) {
   
    List<RetailerDetailsDTO> productValue = Optional.ofNullable(
        productStockrepository.findProductDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate))
        .orElse(Collections.emptyList());

    List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
        retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate))
        .orElse(Collections.emptyList());

    List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
        retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate))
        .orElse(Collections.emptyList());

    List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
    combinedDetails.addAll(productValue);
    combinedDetails.addAll(paymentValue);
    combinedDetails.addAll(commissionValue);

    combinedDetails.sort(Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

    return combinedDetails;
}

    public List<RetailerDetailsDTO> getDetailsByRetailerAndSalesPerson(
            String retailerName, String salesPerson) {
                ClosingSetup lastClosingSetup = closingSetupRepository.findLastClosingSetup();
    
                if (lastClosingSetup != null) {
                    LocalDate startDate = lastClosingSetup.getStartDate();
                    LocalDate endDate = lastClosingSetup.getEndDate();
            
                    List<RetailerDetailsDTO> productValue = Optional.ofNullable(
                        productStockrepository.findProductDetailsBySalesPersonAndRetailerName(salesPerson, retailerName, startDate, endDate))
                        .orElse(Collections.emptyList());
            
                    List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
                        retailerPaymentRepository.findPaymentDetailsBySalesPersonAndRetailerName(salesPerson, retailerName, startDate, endDate))
                        .orElse(Collections.emptyList());
            
                    List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
                        retailerCommissionRepository.findCommissionDetailsBySalesPersonAndRetailerName(salesPerson, retailerName, startDate, endDate))
                        .orElse(Collections.emptyList());
            
                    List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
                    combinedDetails.addAll(productValue);
                    combinedDetails.addAll(paymentValue);
                    combinedDetails.addAll(commissionValue);
            
                    combinedDetails.sort(Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));
            
                    return combinedDetails;
                }
            
                return Collections.emptyList();
    }

    public List<RetailerDetailsDTO> getSalesDatewiseDetailsByRetailerAndSalesPerson(
             String salesPerson, String retailerName, LocalDate startDate, LocalDate endDate) {
              
                    List<RetailerDetailsDTO> productValue = Optional.ofNullable(
                        productStockrepository.findProductDetailsBySalesPersonAndRetailerName(salesPerson, retailerName, startDate, endDate))
                        .orElse(Collections.emptyList());
            
                    List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
                        retailerPaymentRepository.findPaymentDetailsBySalesPersonAndRetailerName(salesPerson, retailerName, startDate, endDate))
                        .orElse(Collections.emptyList());
            
                    List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
                        retailerCommissionRepository.findCommissionDetailsBySalesPersonAndRetailerName(salesPerson, retailerName, startDate, endDate))
                        .orElse(Collections.emptyList());
            
                    List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
                    combinedDetails.addAll(productValue);
                    combinedDetails.addAll(paymentValue);
                    combinedDetails.addAll(commissionValue);
            
                    combinedDetails.sort(Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));
            
                    return combinedDetails;
            
    }


    public RetailerInfo updateRetailerInfo(Long id, RetailerInfo updatedRetailerInfo) {
        Optional<RetailerInfo> existingRetailerOpt = retailerInfoRepository.findById(id);
    
        if (existingRetailerOpt.isPresent()) {
            RetailerInfo existingRetailer = existingRetailerOpt.get();
    
            // Check if the updated retailer name already exists (excluding the current retailer)
            boolean retailerNameExists = retailerInfoRepository.existsByRetailerNameAndIdNot(
                    updatedRetailerInfo.getRetailerName(), id);
    
            if (retailerNameExists) {
           
                throw new RuntimeException("Retailer name '" + updatedRetailerInfo.getRetailerName() 
                    + "' already exists. Update failed.");
            }
    
            // Update retailer info
            existingRetailer.setRetailerName(updatedRetailerInfo.getRetailerName());
            existingRetailer.setRetailerCode(updatedRetailerInfo.getRetailerCode());
            existingRetailer.setThanaName(updatedRetailerInfo.getThanaName());
            existingRetailer.setZillaName(updatedRetailerInfo.getZillaName());
            existingRetailer.setAreaName(updatedRetailerInfo.getAreaName());
            existingRetailer.setMobileNumber(updatedRetailerInfo.getMobileNumber());
            existingRetailer.setSalesPerson(updatedRetailerInfo.getSalesPerson());
            existingRetailer.setStatus(updatedRetailerInfo.getStatus());
    
            return retailerInfoRepository.save(existingRetailer);
        } else {
            throw new RuntimeException("Retailer not found with ID: " + id);
        }
    }
    
}
