package com.example.bake_boss_backend.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.dto.EmployeeMonthlySummary;
import com.example.bake_boss_backend.entity.EmployeeTarget;
import com.example.bake_boss_backend.entity.RetailerInfo;
import com.example.bake_boss_backend.repository.EmployeeTargetRepository;
import com.example.bake_boss_backend.repository.ProductStockrepository;
import com.example.bake_boss_backend.repository.RetailerInfoRepository;
import com.example.bake_boss_backend.repository.RetailerPaymentRepository;

@Service
public class EmployeeTargetService {
     @Autowired
    private EmployeeTargetRepository employeeTargetRepository;

    @Autowired
    private ProductStockrepository productStockRepository;

    @Autowired
    private RetailerPaymentRepository retailerPaymentRepository;

    @Autowired
    private RetailerInfoRepository retailerInfoRepository;

    public List<EmployeeMonthlySummary> getAllEmployeeMonthlySummaries() {
        // Fetch all employee target records
        List<EmployeeTarget> employeeTargets = employeeTargetRepository.findAll();

        // Prepare the list of monthly summaries
        List<EmployeeMonthlySummary> summaries = new ArrayList<>();

        // Loop through each employee target record
        for (EmployeeTarget target : employeeTargets) {
            String employeeName = target.getEmployeeName();
            int year = target.getYear();
            int month = target.getMonth();

            // Find all retailers associated with this employee
            List<RetailerInfo> retailerInfos = retailerInfoRepository.findBySalesPerson(employeeName);
            List<String> retailerNames = new ArrayList<>();
            for (RetailerInfo retailer : retailerInfos) {
                retailerNames.add(retailer.getRetailerName());
            }

            // If no retailers are found, continue to the next iteration
            if (retailerNames.isEmpty()) {
                continue;
            }

            // Fetch total product quantity and sold value for the associated retailers
            Double productQty = productStockRepository.getTotalProductQtyForRetailers(retailerNames, year, month);
            productQty = (productQty == null) ? 0.0 : productQty;

            Double soldValue = productStockRepository.getSoldValueForRetailers(retailerNames, year, month);
            soldValue = (soldValue == null) ? 0.0 : soldValue;

            // Fetch payment value for the associated retailers
            Double paymentValue = retailerPaymentRepository.getPaymentValueForRetailers(retailerNames, year, month);
            paymentValue = (paymentValue == null) ? 0.0 : paymentValue;

            // Create a summary object
            EmployeeMonthlySummary summary = new EmployeeMonthlySummary(
                    employeeName, year, month, target.getAmount(), productQty, soldValue, paymentValue);

            // Add the summary to the list
            summaries.add(summary);
        }

        // Return the list of summaries
        return summaries;
    }
    public List<EmployeeMonthlySummary> getEmployeeMonthlySummaries(String employeeName) {
        // Fetch all employee target records
        List<EmployeeTarget> employeeTargets = employeeTargetRepository.findByEmployeeName(employeeName);

        // Prepare the list of monthly summaries
        List<EmployeeMonthlySummary> summaries = new ArrayList<>();

        // Loop through each employee target record
        for (EmployeeTarget target : employeeTargets) {
            int year = target.getYear();
            int month = target.getMonth();

            // Find all retailers associated with this employee
            List<RetailerInfo> retailerInfos = retailerInfoRepository.findBySalesPerson(employeeName);
            List<String> retailerNames = new ArrayList<>();
            for (RetailerInfo retailer : retailerInfos) {
                retailerNames.add(retailer.getRetailerName());
            }

            // If no retailers are found, continue to the next iteration
            if (retailerNames.isEmpty()) {
                continue;
            }

            // Fetch total product quantity and sold value for the associated retailers
            Double productQty = productStockRepository.getTotalProductQtyForRetailers(retailerNames, year, month);
            productQty = (productQty == null) ? 0.0 : productQty;

            Double soldValue = productStockRepository.getSoldValueForRetailers(retailerNames, year, month);
            soldValue = (soldValue == null) ? 0.0 : soldValue;

            // Fetch payment value for the associated retailers
            Double paymentValue = retailerPaymentRepository.getPaymentValueForRetailers(retailerNames, year, month);
            paymentValue = (paymentValue == null) ? 0.0 : paymentValue;

            // Create a summary object
            EmployeeMonthlySummary summary = new EmployeeMonthlySummary(
                    employeeName, year, month, target.getAmount(), productQty, soldValue, paymentValue);

            // Add the summary to the list
            summaries.add(summary);
        }

        // Return the list of summaries
        return summaries;
    }
}
