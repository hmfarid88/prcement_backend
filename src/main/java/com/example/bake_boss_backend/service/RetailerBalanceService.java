package com.example.bake_boss_backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bake_boss_backend.dto.CategoryBalanceDTO;
import com.example.bake_boss_backend.dto.CategoryDebitCreditDTO;
import com.example.bake_boss_backend.dto.RetailerBalanceDTO;
import com.example.bake_boss_backend.dto.RetailerDetailsDTO;
import com.example.bake_boss_backend.entity.EmployeeInfo;
import com.example.bake_boss_backend.entity.EmployeePayment;
import com.example.bake_boss_backend.entity.EmployeeTarget;
import com.example.bake_boss_backend.entity.RetailerInfo;
import com.example.bake_boss_backend.entity.UserInfo;
import com.example.bake_boss_backend.repository.EmployeeInfoRepository;
import com.example.bake_boss_backend.repository.EmployeePaymentRepository;
import com.example.bake_boss_backend.repository.EmployeeTargetRepository;
import com.example.bake_boss_backend.repository.ProductStockrepository;
import com.example.bake_boss_backend.repository.RetailerCommissionRepository;
import com.example.bake_boss_backend.repository.RetailerInfoRepository;
import com.example.bake_boss_backend.repository.RetailerPaymentRepository;
import com.example.bake_boss_backend.repository.UserInfoRepository;

import jakarta.transaction.Transactional;

@Service
public class RetailerBalanceService {
    @Autowired
    private RetailerPaymentRepository retailerPaymentRepository;

    @Autowired
    private RetailerInfoRepository retailerInfoRepository;

    @Autowired
    private EmployeeInfoRepository employeeInfoRepository;

    @Autowired
    private ProductStockrepository productStockrepository;

    @Autowired
    private RetailerCommissionRepository retailerCommissionRepository;

    @Autowired
    private EmployeePaymentRepository employeePaymentRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private EmployeeTargetRepository employeeTargetRepository;

    public List<RetailerBalanceDTO> retailerBalance() {
       
            List<RetailerBalanceDTO> list = retailerPaymentRepository.findRetailerBalance();

            for (RetailerBalanceDTO dto : list) {

                double productValue = dto.getTotalProductValue() != null ? dto.getTotalProductValue() : 0;
                double payment = dto.getTotalPayment() != null ? dto.getTotalPayment() : 0;
                double commission = dto.getTotalCommission() != null ? dto.getTotalCommission() : 0;
                double balance = productValue - (payment + commission);

                if (balance > 0) {
                    dto.setCredit(balance);
                    dto.setDebit(0.0);
                } else {
                    dto.setDebit(Math.abs(balance));
                    dto.setCredit(0.0);
                }
            }

            return list;
       
    }

    public List<RetailerBalanceDTO> datewiseRetailerBalance(LocalDate startDate, LocalDate endDate) {

        List<RetailerBalanceDTO> list = retailerPaymentRepository
                .findRetailerBalanceBetweenDates(startDate, endDate);

        for (RetailerBalanceDTO dto : list) {

            double productValue = dto.getTotalProductValue() != null ? dto.getTotalProductValue() : 0;
            double payment = dto.getTotalPayment() != null ? dto.getTotalPayment() : 0;
            double commission = dto.getTotalCommission() != null ? dto.getTotalCommission() : 0;

            double balance = productValue - (payment + commission);

            if (balance > 0) {
                dto.setCredit(balance);
                dto.setDebit(0.0);
            } else {
                dto.setDebit(Math.abs(balance));
                dto.setCredit(0.0);
            }
        }

        return list;
    }

    
    // public List<RetailerBalanceDTO> salesDatewiseRetailerBalance(String salesPerson, LocalDate startDate,
    //         LocalDate endDate) {
    //     return retailerPaymentRepository.findSalesRetailerBalanceBetweenDates(salesPerson, startDate, endDate);
    // }

    public List<RetailerDetailsDTO> retailerDetailsForCurrentMonth(String retailerName) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
            // 1️⃣ Calculate opening balance (all transactions before this month)
            List<RetailerDetailsDTO> prevProduct = Optional.ofNullable(
                    productStockrepository.findProductDetailsByUsernameAndRetailerNameBeforemonth(retailerName, year, month))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> prevPayment = Optional.ofNullable(
                    retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerNameBeforemonth(retailerName, year, month))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> prevCommission = Optional.ofNullable(
                    retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerNameBeforemonth(retailerName, year, month))
                    .orElse(Collections.emptyList());

            double openingBalance = 0.0;

            for (RetailerDetailsDTO dto : prevProduct) {
                openingBalance += dto.getProductValue() != null ? dto.getProductValue() : 0.0;
            }
            for (RetailerDetailsDTO dto : prevPayment) {
                openingBalance -= dto.getPayment() != null ? dto.getPayment() : 0.0;
            }
            for (RetailerDetailsDTO dto : prevCommission) {
                openingBalance -= dto.getCommission() != null ? dto.getCommission() : 0.0;
            }

            // 2️⃣ Fetch current month data
            List<RetailerDetailsDTO> productValue = Optional.ofNullable(
                    productStockrepository.findProductDetailsByUsernameAndRetailerNameCurrentmonth(retailerName, year, month))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
                    retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerNameCurrentmonth(retailerName, year, month))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
                    retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerNameCurrentmonth(retailerName, year, month))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
            combinedDetails.addAll(productValue);
            combinedDetails.addAll(paymentValue);
            combinedDetails.addAll(commissionValue);

            // 3️⃣ Sort current month rows by date
            combinedDetails.sort(
                    Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

            // 4️⃣ Prepare final result with opening balance
            List<RetailerDetailsDTO> finalResult = new ArrayList<>();

            RetailerDetailsDTO openingRow = new RetailerDetailsDTO(
                    now.withDayOfMonth(1).minusDays(1), // last day of prev month
                    "Opening Balance", null, null, null, null, null, null, openingBalance);
            finalResult.add(openingRow);

            // 5️⃣ Running balance for current month
            double runningBalance = openingBalance;
            for (RetailerDetailsDTO dto : combinedDetails) {
                double value = 0.0;

                if (dto.getProductValue() != null) {
                    value += dto.getProductValue();
                }
                if (dto.getPayment() != null) {
                    value -= dto.getPayment();
                }
                if (dto.getCommission() != null) {
                    value -= dto.getCommission();
                }

                runningBalance += value;
                dto.setBalance(runningBalance);
                finalResult.add(dto);
            }

            return finalResult;
           }

    public List<RetailerDetailsDTO> retailerDetailsDatetodate(String retailerName, LocalDate startDate, LocalDate endDate) {

            // 1️⃣ Calculate opening balance (all transactions before this month)
            List<RetailerDetailsDTO> prevProduct = Optional.ofNullable(
                    productStockrepository.findProductDetailsByUsernameAndRetailerNameBeforeDate(retailerName, startDate))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> prevPayment = Optional.ofNullable(
                    retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerNameBeforedate(retailerName, startDate))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> prevCommission = Optional.ofNullable(
                    retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerNameBeforeDate(retailerName, startDate))
                    .orElse(Collections.emptyList());

            double openingBalance = 0.0;

            for (RetailerDetailsDTO dto : prevProduct) {
                openingBalance += dto.getProductValue() != null ? dto.getProductValue() : 0.0;
            }
            for (RetailerDetailsDTO dto : prevPayment) {
                openingBalance -= dto.getPayment() != null ? dto.getPayment() : 0.0;
            }
            for (RetailerDetailsDTO dto : prevCommission) {
                openingBalance -= dto.getCommission() != null ? dto.getCommission() : 0.0;
            }

            // 2️⃣ Fetch current month data
            List<RetailerDetailsDTO> productValue = Optional.ofNullable(
                    productStockrepository.findProductDetailsByUsernameAndRetailerNameDatetodate(retailerName, startDate, endDate))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
                    retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerNameDatetodate(retailerName, startDate, endDate))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
                    retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerNameDatetoDate(retailerName, startDate, endDate))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
            combinedDetails.addAll(productValue);
            combinedDetails.addAll(paymentValue);
            combinedDetails.addAll(commissionValue);

            // 3️⃣ Sort current month rows by date
            combinedDetails.sort(
                    Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

            // 4️⃣ Prepare final result with opening balance
            List<RetailerDetailsDTO> finalResult = new ArrayList<>();

            RetailerDetailsDTO openingRow = new RetailerDetailsDTO(
                    startDate.withDayOfMonth(1).minusDays(1), // last day of prev month
                    "Opening Balance", null, null, null, null, null, null, openingBalance);
            finalResult.add(openingRow);

            // 5️⃣ Running balance for current month
            double runningBalance = openingBalance;
            for (RetailerDetailsDTO dto : combinedDetails) {
                double value = 0.0;

                if (dto.getProductValue() != null) {
                    value += dto.getProductValue();
                }
                if (dto.getPayment() != null) {
                    value -= dto.getPayment();
                }
                if (dto.getCommission() != null) {
                    value -= dto.getCommission();
                }

                runningBalance += value;
                dto.setBalance(runningBalance);
                finalResult.add(dto);
            }

            return finalResult;
      
    }

    public List<CategoryDebitCreditDTO> getCategoryWiseDebitCredit() {
        List<CategoryBalanceDTO> currentMonthData = retailerPaymentRepository.findCategoryRawBalanceForCurrentMonth();
        List<CategoryBalanceDTO> openingData = retailerPaymentRepository.findCategoryOpeningBalanceBeforeCurrentMonth();

        Map<String, CategoryDebitCreditDTO> categoryMap = new HashMap<>();

        // Step 1: Compute opening balances first
        for (CategoryBalanceDTO dto : openingData) {
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);
            double openingBalance = productValue - totalPaid;

            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), 0.0, 0.0, 0.0, 0.0);
            }
            existing.setOpeningBalance(existing.getOpeningBalance() + openingBalance);
            categoryMap.put(dto.getCategory(), existing);
        }

        // Step 2: Compute debit/credit for current month
        for (CategoryBalanceDTO dto : currentMonthData) {
            double productQty = Optional.ofNullable(dto.getTotalProductQty()).orElse(0.0);
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);

            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), productQty, productValue, totalPaid, 0.0);
            } else {
                existing.setQty(existing.getQty() + productQty);
                existing.setDebit(existing.getDebit() + productValue);
                existing.setCredit(existing.getCredit() + totalPaid);
            }
            categoryMap.put(dto.getCategory(), existing);
        }

        // return new ArrayList<>(categoryMap.values());
        return categoryMap.values().stream()
                .sorted(Comparator.comparing(
                        CategoryDebitCreditDTO::getCategory,
                        Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER)))
                .toList();

    }

    public List<CategoryDebitCreditDTO> getMarketWiseDebitCredit(String category) {
        List<CategoryBalanceDTO> currentMonthData = retailerPaymentRepository
                .findMarketingBalanceForCurrentMonth(category);
        List<CategoryBalanceDTO> openingData = retailerPaymentRepository
                .findMarketOpeningBalanceBeforeCurrentMonth(category);

        Map<String, CategoryDebitCreditDTO> categoryMap = new HashMap<>();

        // Step 1: Compute opening balances first
        for (CategoryBalanceDTO dto : openingData) {
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);
            double openingBalance = productValue - totalPaid;

            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), 0.0, 0.0, 0.0, 0.0);
            }
            existing.setOpeningBalance(existing.getOpeningBalance() + openingBalance);
            categoryMap.put(dto.getCategory(), existing);
        }

        // Step 2: Compute debit/credit for current month
        for (CategoryBalanceDTO dto : currentMonthData) {
            double productQty = Optional.ofNullable(dto.getTotalProductQty()).orElse(0.0);
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);

            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), productQty, productValue, totalPaid, 0.0);
            } else {
                existing.setQty(existing.getQty() + productQty);
                existing.setDebit(existing.getDebit() + productValue);
                existing.setCredit(existing.getCredit() + totalPaid);
            }
            categoryMap.put(dto.getCategory(), existing);
        }

        return new ArrayList<>(categoryMap.values());
    }

    public List<CategoryDebitCreditDTO> getDatewiseMarketWiseDebitCredit(String category, LocalDate startDate,
            LocalDate endDate) {
        List<CategoryBalanceDTO> currentMonthData = retailerPaymentRepository.findDatewiseMarketingBalance(category,
                startDate, endDate);
        List<CategoryBalanceDTO> openingData = retailerPaymentRepository.findMarketOpeningBalanceBeforeDate(category,
                startDate);

        Map<String, CategoryDebitCreditDTO> categoryMap = new HashMap<>();

        // Step 1: Compute opening balances first
        for (CategoryBalanceDTO dto : openingData) {
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);
            double openingBalance = productValue - totalPaid;

            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), 0.0, 0.0, 0.0, 0.0);
            }
            existing.setOpeningBalance(existing.getOpeningBalance() + openingBalance);
            categoryMap.put(dto.getCategory(), existing);
        }

        // Step 2: Compute debit/credit for current month
        for (CategoryBalanceDTO dto : currentMonthData) {
            double productQty = Optional.ofNullable(dto.getTotalProductQty()).orElse(0.0);
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);

            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), productQty, productValue, totalPaid, 0.0);
            } else {
                existing.setQty(existing.getQty() + productQty);
                existing.setDebit(existing.getDebit() + productValue);
                existing.setCredit(existing.getCredit() + totalPaid);
            }
            categoryMap.put(dto.getCategory(), existing);
        }

        return new ArrayList<>(categoryMap.values());
    }

    public List<CategoryDebitCreditDTO> getMarketRetailerWiseDebitCredit(String salesPerson) {
        List<CategoryBalanceDTO> currentMonthData = retailerPaymentRepository
                .findMarketingRetailerBalanceForCurrentMonth(salesPerson);
        List<CategoryBalanceDTO> openingData = retailerPaymentRepository
                .findMarketRetailerOpeningBalanceBeforeCurrentMonth(salesPerson);

        Map<String, CategoryDebitCreditDTO> categoryMap = new HashMap<>();

        // Step 1: Compute opening balances first
        for (CategoryBalanceDTO dto : openingData) {
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);
            double openingBalance = productValue - totalPaid;

            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), 0.0, 0.0, 0.0, 0.0);
            }
            existing.setOpeningBalance(existing.getOpeningBalance() + openingBalance);
            categoryMap.put(dto.getCategory(), existing);
        }

        // Step 2: Compute debit/credit for current month
        for (CategoryBalanceDTO dto : currentMonthData) {
            double productQty = Optional.ofNullable(dto.getTotalProductQty()).orElse(0.0);
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);

            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), productQty, productValue, totalPaid, 0.0);
            } else {
                existing.setQty(existing.getQty() + productQty);
                existing.setDebit(existing.getDebit() + productValue);
                existing.setCredit(existing.getCredit() + totalPaid);
            }
            categoryMap.put(dto.getCategory(), existing);
        }

        return new ArrayList<>(categoryMap.values());
    }

    public List<CategoryDebitCreditDTO> getDatewiseMarketRetailerWiseDebitCredit(String salesPerson,
            LocalDate startDate, LocalDate endDate) {
        List<CategoryBalanceDTO> currentMonthData = retailerPaymentRepository
                .findDatewiseMarketingRetailerBalance(salesPerson, startDate, endDate);
        List<CategoryBalanceDTO> openingData = retailerPaymentRepository
                .findMarketRetailerOpeningBalanceBeforeDate(salesPerson, startDate);

        Map<String, CategoryDebitCreditDTO> categoryMap = new HashMap<>();

        // Step 1: Compute opening balances first
        for (CategoryBalanceDTO dto : openingData) {
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);
            double openingBalance = productValue - totalPaid;

            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), 0.0, 0.0, 0.0, 0.0);
            }
            existing.setOpeningBalance(existing.getOpeningBalance() + openingBalance);
            categoryMap.put(dto.getCategory(), existing);
        }

        // Step 2: Compute debit/credit for current month
        for (CategoryBalanceDTO dto : currentMonthData) {
            double productQty = Optional.ofNullable(dto.getTotalProductQty()).orElse(0.0);
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);
            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), productQty, productValue, totalPaid, 0.0);
            } else {
                existing.setQty(existing.getQty() + productQty);
                existing.setDebit(existing.getDebit() + productValue);
                existing.setCredit(existing.getCredit() + totalPaid);
            }
            categoryMap.put(dto.getCategory(), existing);
        }

        return new ArrayList<>(categoryMap.values());
    }

    // datewise
    public List<CategoryDebitCreditDTO> getdatewiseCategoryWiseDebitCredit(LocalDate startDate, LocalDate endDate) {
        List<CategoryBalanceDTO> currentMonthData = retailerPaymentRepository.findDatewiseCategoryBalance(startDate,
                endDate);
        List<CategoryBalanceDTO> openingData = retailerPaymentRepository
                .findCategoryOpeningBalanceBeforeDate(startDate);

        Map<String, CategoryDebitCreditDTO> categoryMap = new HashMap<>();

        // Step 1: Compute opening balances first
        for (CategoryBalanceDTO dto : openingData) {
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);
            double openingBalance = productValue - totalPaid;

            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), 0.0, 0.0, 0.0, 0.0);
            }
            existing.setOpeningBalance(existing.getOpeningBalance() + openingBalance);
            categoryMap.put(dto.getCategory(), existing);
        }

        for (CategoryBalanceDTO dto : currentMonthData) {
            double productQty = Optional.ofNullable(dto.getTotalProductQty()).orElse(0.0);
            double productValue = Optional.ofNullable(dto.getTotalProductValue()).orElse(0.0);
            double totalPaid = Optional.ofNullable(dto.getTotalPayment()).orElse(0.0)
                    + Optional.ofNullable(dto.getTotalCommission()).orElse(0.0);
            CategoryDebitCreditDTO existing = categoryMap.get(dto.getCategory());
            if (existing == null) {
                existing = new CategoryDebitCreditDTO(dto.getCategory(), productQty, productValue, totalPaid, 0.0);
            } else {
                existing.setQty(existing.getQty() + productQty);
                existing.setDebit(existing.getDebit() + productValue);
                existing.setCredit(existing.getCredit() + totalPaid);
            }
            categoryMap.put(dto.getCategory(), existing);
        }

        return categoryMap.values().stream()
                .sorted(Comparator.comparing(
                        CategoryDebitCreditDTO::getCategory,
                        Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER)))
                .toList();

    }
    
    // public RetailerInfo updateRetailerInfo(Long id, RetailerInfo updatedRetailerInfo) {
    //     Optional<RetailerInfo> existingRetailerOpt = retailerInfoRepository.findById(id);

    //     if (existingRetailerOpt.isPresent()) {
    //         RetailerInfo existingRetailer = existingRetailerOpt.get();

    //         // Check if the updated retailer name already exists (excluding the current
    //         // retailer)
    //         boolean retailerNameExists = retailerInfoRepository.existsByRetailerNameAndIdNot(
    //                 updatedRetailerInfo.getRetailerName(), id);

    //         if (retailerNameExists) {

    //             throw new RuntimeException("Retailer name '" + updatedRetailerInfo.getRetailerName()
    //                     + "' already exists. Update failed.");
    //         }

    //         // Update retailer info
    //         existingRetailer.setCategory(updatedRetailerInfo.getCategory());
    //         existingRetailer.setRetailerName(updatedRetailerInfo.getRetailerName());
    //         existingRetailer.setRetailerCode(updatedRetailerInfo.getRetailerCode());
    //         existingRetailer.setThanaName(updatedRetailerInfo.getThanaName());
    //         existingRetailer.setZillaName(updatedRetailerInfo.getZillaName());
    //         existingRetailer.setAreaName(updatedRetailerInfo.getAreaName());
    //         existingRetailer.setMobileNumber(updatedRetailerInfo.getMobileNumber());
    //         existingRetailer.setSalesPerson(updatedRetailerInfo.getSalesPerson());
    //         existingRetailer.setStatus(updatedRetailerInfo.getStatus());

    //         return retailerInfoRepository.save(existingRetailer);
    //     } else {
    //         throw new RuntimeException("Retailer not found with ID: " + id);
    //     }
    // }

@Transactional
public RetailerInfo updateRetailerInfo(Long id, RetailerInfo updatedRetailerInfo) {

    Optional<RetailerInfo> existingRetailerOpt = retailerInfoRepository.findById(id);

    if (existingRetailerOpt.isPresent()) {

        RetailerInfo existingRetailer = existingRetailerOpt.get();

        // store old retailer name
        String oldRetailerName = existingRetailer.getRetailerName();

        // check duplicate name
        boolean retailerNameExists =
                retailerInfoRepository.existsByRetailerNameAndIdNot(
                        updatedRetailerInfo.getRetailerName(),
                        id
                );

        if (retailerNameExists) {
            throw new RuntimeException(
                    "Retailer name '" +
                    updatedRetailerInfo.getRetailerName() +
                    "' already exists. Update failed."
            );
        }

        // update retailer info
        existingRetailer.setCategory(updatedRetailerInfo.getCategory());
        existingRetailer.setRetailerName(updatedRetailerInfo.getRetailerName());
        existingRetailer.setRetailerCode(updatedRetailerInfo.getRetailerCode());
        existingRetailer.setThanaName(updatedRetailerInfo.getThanaName());
        existingRetailer.setZillaName(updatedRetailerInfo.getZillaName());
        existingRetailer.setAreaName(updatedRetailerInfo.getAreaName());
        existingRetailer.setMobileNumber(updatedRetailerInfo.getMobileNumber());
        existingRetailer.setSalesPerson(updatedRetailerInfo.getSalesPerson());
        existingRetailer.setStatus(updatedRetailerInfo.getStatus());

        // save retailer first
        RetailerInfo savedRetailer = retailerInfoRepository.save(existingRetailer);

        // update related tables
        productStockrepository.updateCustomerName(oldRetailerName, updatedRetailerInfo.getRetailerName());

        retailerPaymentRepository.updateRetailerName(oldRetailerName, updatedRetailerInfo.getRetailerName());

        retailerCommissionRepository.updateRetailerName(oldRetailerName, updatedRetailerInfo.getRetailerName());

        return savedRetailer;

    } else {
        throw new RuntimeException("Retailer not found with ID: " + id);
    }
}

    public EmployeeInfo updateEmployeeInfo(Long id, EmployeeInfo updatedEmployeeInfo) {
        Optional<EmployeeInfo> existingRetailerOpt = employeeInfoRepository.findById(id);

        if (existingRetailerOpt.isPresent()) {
            EmployeeInfo existingRetailer = existingRetailerOpt.get();

            // Check if the updated retailer name already exists (excluding the current
            // retailer)
            boolean retailerNameExists = employeeInfoRepository
                    .existsByEmployeeNameAndIdNot(updatedEmployeeInfo.getEmployeeName(), id);

            if (retailerNameExists) {

                throw new RuntimeException("Employee name '" + updatedEmployeeInfo.getEmployeeName()
                        + "' already exists. Update failed.");
            }
            String oldEmployeeName = existingRetailer.getEmployeeName();
            String newEmployeeName = updatedEmployeeInfo.getEmployeeName();
            // Update retailer info
            existingRetailer.setEmployeeName(updatedEmployeeInfo.getEmployeeName());
            existingRetailer.setFatherName(updatedEmployeeInfo.getFatherName());
            existingRetailer.setAddress(updatedEmployeeInfo.getAddress());
            existingRetailer.setPhoneNumber(updatedEmployeeInfo.getPhoneNumber());

            UserInfo userInfo = userInfoRepository.findByUsername(oldEmployeeName);
            List<RetailerInfo> retailerInfo = retailerInfoRepository.findBySalesPerson(oldEmployeeName);
            List<EmployeePayment> employeePayments = employeePaymentRepository.findByEmployeeName(oldEmployeeName);
            List<EmployeeTarget> employeeTargets = employeeTargetRepository.findByEmployeeName(oldEmployeeName);
            if (userInfo != null) {
                userInfo.setUsername(newEmployeeName);
                userInfoRepository.save(userInfo);
            }
            if (retailerInfo != null) {
                for (RetailerInfo info : retailerInfo) {
                    info.setSalesPerson(newEmployeeName);
                }
                retailerInfoRepository.saveAll(retailerInfo);
            }
            if (employeePayments != null) {
                for (EmployeePayment payments : employeePayments) {
                    payments.setEmployeeName(newEmployeeName);
                }
                employeePaymentRepository.saveAll(employeePayments);
            }
            if (employeeTargets != null) {
                for (EmployeeTarget employeeTarget : employeeTargets) {
                    employeeTarget.setEmployeeName(newEmployeeName);
                }
                employeeTargetRepository.saveAll(employeeTargets);
            }
            return employeeInfoRepository.save(existingRetailer);
        } else {
            throw new RuntimeException("Employee not found with ID: " + id);
        }
    }

    public Double getRetailerDue() {

        Double totalSoldValue = productStockrepository.getTotalSoldValueByRetailer();

        Double totalPayment = retailerPaymentRepository.getTotalRetailerPayment();

        Double totalCommission = retailerCommissionRepository.getTotalRetailerCommission();

        return totalSoldValue - totalPayment - totalCommission;
    }
}
