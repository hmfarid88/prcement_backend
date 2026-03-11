package com.example.bake_boss_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.dto.DetailsPayReceiveDTO;
import com.example.bake_boss_backend.dto.FinanceBalanceDTO;
import com.example.bake_boss_backend.repository.OfficePaymentRepository;
import com.example.bake_boss_backend.repository.OfficeReceiveRepository;

@Service
public class FinanceService {
    private final OfficePaymentRepository officePaymentRepository;
    private final OfficeReceiveRepository officeReceiveRepository;

    public FinanceService(OfficePaymentRepository officePaymentRepository, OfficeReceiveRepository officeReceiveRepository) {
        this.officePaymentRepository = officePaymentRepository;
        this.officeReceiveRepository = officeReceiveRepository;
    }

    public List<FinanceBalanceDTO> getFinanceBalance() {
        Map<String, Double> paymentMap = new HashMap<>();
        Map<String, Double> receiveMap = new HashMap<>();

        // Fetch total payments and store them in a map
        for (Object[] obj : officePaymentRepository.findTotalPaymentAmountGroupedByPaymentName()) {
            paymentMap.put((String) obj[0], (Double) obj[1]);
        }

        // Fetch total receives and store them in a map
        for (Object[] obj : officeReceiveRepository.findTotalReceiveAmountGroupedByReceiveName()) {
            receiveMap.put((String) obj[0], (Double) obj[1]);
        }

        // Get all unique names from both maps
        Set<String> allNames = new HashSet<>();
        allNames.addAll(paymentMap.keySet());
        allNames.addAll(receiveMap.keySet());

        // Prepare the final list with calculated balances
        List<FinanceBalanceDTO> financeBalances = new ArrayList<>();
        for (String name : allNames) {
            Double totalPayment = paymentMap.getOrDefault(name, 0.0);
            Double totalReceive = receiveMap.getOrDefault(name, 0.0);
            Double balance = totalPayment - totalReceive; // Subtract payments from receives

            financeBalances.add(new FinanceBalanceDTO(name, totalPayment, totalReceive, balance));
        }

        return financeBalances;
    }

    
    // public List<DetailsPayReceiveDTO> detailsPayReceive(String username, String name) {
    //     List<DetailsPayReceiveDTO> payments = officePaymentRepository.findDetailsPaymentByPaymentNameAndUsername(username, name);
    //     List<DetailsPayReceiveDTO> receives = officeReceiveRepository.findDetailsReceiveByReceiveNameAndUsername(username, name);
        
    //     List<DetailsPayReceiveDTO> combinedPayments = new ArrayList<>();
    //     combinedPayments.addAll(payments);
    //     combinedPayments.addAll(receives);
        
    //     // Sort the combined list by date
    //     combinedPayments.sort(Comparator.comparing(DetailsPayReceiveDTO::getDate));
        
    //     return combinedPayments;
    // }
    
    public List<DetailsPayReceiveDTO> detailsPayReceive(String username, String name) {

    LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

    // previous balance
    Double prevPayment = officePaymentRepository.findPreviousPayment(username, name, firstDayOfMonth);
    Double prevReceive = officeReceiveRepository.findPreviousReceive(username, name, firstDayOfMonth);

    double openingBalance = prevReceive - prevPayment;

    List<DetailsPayReceiveDTO> result = new ArrayList<>();

    // Opening Balance Row
    result.add(new DetailsPayReceiveDTO(firstDayOfMonth, "Opening Balance", 
            openingBalance < 0 ? Math.abs(openingBalance) : 0.0,
            openingBalance > 0 ? openingBalance : 0.0));

    // Current month data
    List<DetailsPayReceiveDTO> payments =
            officePaymentRepository.findDetailsPaymentByPaymentNameAndUsername(username, name, firstDayOfMonth);

    List<DetailsPayReceiveDTO> receives =
            officeReceiveRepository.findDetailsReceiveByReceiveNameAndUsername(username, name, firstDayOfMonth);

    result.addAll(payments);
    result.addAll(receives);

    // Sort by date
    result.sort(Comparator.comparing(DetailsPayReceiveDTO::getDate));

    return result;
}

    public List<DetailsPayReceiveDTO> datewiseDetailsPayReceive(String username, String name, LocalDate startDate, LocalDate endDate) {

    // previous balance
    Double prevPayment = officePaymentRepository.findPreviousPayment(username, name, startDate);
    Double prevReceive = officeReceiveRepository.findPreviousReceive(username, name, startDate);

    double openingBalance = prevReceive - prevPayment;

    List<DetailsPayReceiveDTO> result = new ArrayList<>();

    // Opening Balance Row
    result.add(new DetailsPayReceiveDTO(startDate, "Opening Balance", 
            openingBalance < 0 ? Math.abs(openingBalance) : 0.0,
            openingBalance > 0 ? openingBalance : 0.0));

    // Current month data
    List<DetailsPayReceiveDTO> payments =
            officePaymentRepository.findDatewiseDetailsPaymentByPaymentNameAndUsername(username, name, startDate, endDate);

    List<DetailsPayReceiveDTO> receives =
            officeReceiveRepository.findDatewiseDetailsReceiveByReceiveNameAndUsername(username, name, startDate, endDate);

    result.addAll(payments);
    result.addAll(receives);

    // Sort by date
    result.sort(Comparator.comparing(DetailsPayReceiveDTO::getDate));

    return result;
}
}
