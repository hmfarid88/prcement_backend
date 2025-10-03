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
import com.example.bake_boss_backend.entity.EmployeeInfo;
import com.example.bake_boss_backend.entity.EmployeePayment;
import com.example.bake_boss_backend.entity.EmployeeTarget;
import com.example.bake_boss_backend.entity.RetailerInfo;
import com.example.bake_boss_backend.entity.UserInfo;
import com.example.bake_boss_backend.repository.ClosingSetupRepository;
import com.example.bake_boss_backend.repository.EmployeeInfoRepository;
import com.example.bake_boss_backend.repository.EmployeePaymentRepository;
import com.example.bake_boss_backend.repository.EmployeeTargetRepository;
import com.example.bake_boss_backend.repository.ProductStockrepository;
import com.example.bake_boss_backend.repository.RetailerCommissionRepository;
import com.example.bake_boss_backend.repository.RetailerInfoRepository;
import com.example.bake_boss_backend.repository.RetailerPaymentRepository;
import com.example.bake_boss_backend.repository.UserInfoRepository;

@Service
public class RetailerBalanceService {
    @Autowired
    private RetailerPaymentRepository retailerPaymentRepository;

    @Autowired
    private ClosingSetupRepository closingSetupRepository;

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

    public List<RetailerBalanceDTO> salesDatewiseRetailerBalance(String salesPerson, LocalDate startDate,
            LocalDate endDate) {
        return retailerPaymentRepository.findSalesRetailerBalanceBetweenDates(salesPerson, startDate, endDate);
    }


public List<RetailerDetailsDTO> retailerDetailsForCurrentMonth(String retailerName, String username) {
    LocalDate now = LocalDate.now();
    int year = now.getYear();
    int month = now.getMonthValue();
    ClosingSetup lastClosingSetup = closingSetupRepository.findLastClosingSetup();

    if (lastClosingSetup != null) {

        // 1️⃣ Calculate opening balance (all transactions before this month)
        List<RetailerDetailsDTO> prevProduct = Optional.ofNullable(
                productStockrepository.findProductDetailsByUsernameAndRetailerNameBeforemonth(username, retailerName, year, month))
                .orElse(Collections.emptyList());

        List<RetailerDetailsDTO> prevPayment = Optional.ofNullable(
                retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerNameBeforemonth(username, retailerName, year, month))
                .orElse(Collections.emptyList());

        List<RetailerDetailsDTO> prevCommission = Optional.ofNullable(
                retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerNameBeforemonth(username, retailerName, year, month))
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
                productStockrepository.findProductDetailsByUsernameAndRetailerNameCurrentmonth(username, retailerName, year, month))
                .orElse(Collections.emptyList());

        List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
                retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerNameCurrentmonth(username, retailerName, year, month))
                .orElse(Collections.emptyList());

        List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
                retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerNameCurrentmonth(username, retailerName, year, month))
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
                "Opening Balance", null, null, null, null, null, null, openingBalance
        );
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

    return Collections.emptyList();
}

public List<RetailerDetailsDTO> retailerDetailsDatetodate(String username, String retailerName, LocalDate startDate, LocalDate endDate) {
   
    ClosingSetup lastClosingSetup = closingSetupRepository.findLastClosingSetup();

    if (lastClosingSetup != null) {

        // 1️⃣ Calculate opening balance (all transactions before this month)
        List<RetailerDetailsDTO> prevProduct = Optional.ofNullable(
                productStockrepository.findProductDetailsByUsernameAndRetailerNameBeforeDate(username, retailerName, startDate))
                .orElse(Collections.emptyList());

        List<RetailerDetailsDTO> prevPayment = Optional.ofNullable(
                retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerNameBeforedate(username, retailerName, startDate))
                .orElse(Collections.emptyList());

        List<RetailerDetailsDTO> prevCommission = Optional.ofNullable(
                retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerNameBeforeDate(username, retailerName, startDate))
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
                productStockrepository.findProductDetailsByUsernameAndRetailerNameDatetodate(username, retailerName, startDate, endDate))
                .orElse(Collections.emptyList());

        List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
                retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerNameDatetodate(username, retailerName, startDate, endDate))
                .orElse(Collections.emptyList());

        List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
                retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerNameDatetoDate(username, retailerName, startDate, endDate))
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
                "Opening Balance", null, null, null, null, null, null, openingBalance
        );
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

    return Collections.emptyList();
}


//     public List<RetailerDetailsDTO> getDatewiseDetailsByRetailerAndUsername(String retailerName, String username) {
//         ClosingSetup lastClosingSetup = closingSetupRepository.findLastClosingSetup();

//         if (lastClosingSetup != null) {
//             LocalDate startDate = lastClosingSetup.getStartDate();
//             LocalDate endDate = lastClosingSetup.getEndDate();

//             List<RetailerDetailsDTO> productValue = Optional.ofNullable(productStockrepository.findProductDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate))
//                     .orElse(Collections.emptyList());

//             List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate))
//                     .orElse(Collections.emptyList());

//             List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate))
//                     .orElse(Collections.emptyList());

//             List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
//             combinedDetails.addAll(productValue);
//             combinedDetails.addAll(paymentValue);
//             combinedDetails.addAll(commissionValue);

//             combinedDetails.sort(
//                     Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.reverseOrder())));

//             return combinedDetails;
//         }

//         return Collections.emptyList();
//     }


// public Page<RetailerDetailsDTO> getDatewiseDetailsByRetailerAndUsername(
//         String retailerName, String username, int page, int size) {

//     ClosingSetup lastClosingSetup = closingSetupRepository.findLastClosingSetup();

//     if (lastClosingSetup != null) {
//         LocalDate startDate = lastClosingSetup.getStartDate();
//         LocalDate endDate = lastClosingSetup.getEndDate();

//         List<RetailerDetailsDTO> productValue = Optional.ofNullable(
//                 productStockrepository.findProductDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate)
//         ).orElse(Collections.emptyList());

//         List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
//                 retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate)
//         ).orElse(Collections.emptyList());

//         List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
//                 retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate)
//         ).orElse(Collections.emptyList());

//         List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
//         combinedDetails.addAll(productValue);
//         combinedDetails.addAll(paymentValue);
//         combinedDetails.addAll(commissionValue);

//         // 1️⃣ Sort ASC first (for correct running balance calculation)
//         combinedDetails.sort(
//                 Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
//         );

//         // 2️⃣ Running balance calculation
//         double balance = 0;
//         for (RetailerDetailsDTO dto : combinedDetails) {
//             double productVal = dto.getProductValue() != null ? dto.getProductValue() : 0;
//             double paymentVal = dto.getPayment() != null ? dto.getPayment() : 0;
//             double commissionVal = dto.getCommission() != null ? dto.getCommission() : 0;

//             balance += productVal;
//             balance -= paymentVal;
//             balance -= commissionVal;

//             dto.setBalance(balance);
//         }

//         // 3️⃣ Sort DESC for display
//         Collections.reverse(combinedDetails);

//         // 4️⃣ Manual pagination
//         int start = Math.min(page * size, combinedDetails.size());
//         int end = Math.min(start + size, combinedDetails.size());
//         List<RetailerDetailsDTO> paginatedList = combinedDetails.subList(start, end);

//         return new PageImpl<>(paginatedList, PageRequest.of(page, size), combinedDetails.size());
//     }

//     return Page.empty();
// }



//     public List<RetailerDetailsDTO> getDatewiseRetailerDetailsByRetailerAndUsername(
//             String retailerName, String username, LocalDate startDate, LocalDate endDate) {

//         List<RetailerDetailsDTO> productValue = Optional.ofNullable(
//                 productStockrepository.findProductDetailsByUsernameAndRetailerName(username, retailerName, startDate,
//                         endDate))
//                 .orElse(Collections.emptyList());

//         List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
//                 retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerName(username, retailerName, startDate,
//                         endDate))
//                 .orElse(Collections.emptyList());

//         List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
//                 retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerName(username, retailerName,
//                         startDate, endDate))
//                 .orElse(Collections.emptyList());

//         List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
//         combinedDetails.addAll(productValue);
//         combinedDetails.addAll(paymentValue);
//         combinedDetails.addAll(commissionValue);

//         combinedDetails.sort(
//                 Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

//         return combinedDetails;
//     }

// public Page<RetailerDetailsDTO> getDatewiseRetailerDetailsByRetailerAndUsername(
//         String retailerName, String username, LocalDate startDate, LocalDate endDate, int page, int size) {

//     List<RetailerDetailsDTO> productValue = Optional.ofNullable(
//             productStockrepository.findProductDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate)
//     ).orElse(Collections.emptyList());

//     List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
//             retailerPaymentRepository.findPaymentDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate)
//     ).orElse(Collections.emptyList());

//     List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
//             retailerCommissionRepository.findCommissionDetailsByUsernameAndRetailerName(username, retailerName, startDate, endDate)
//     ).orElse(Collections.emptyList());

//     List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
//     combinedDetails.addAll(productValue);
//     combinedDetails.addAll(paymentValue);
//     combinedDetails.addAll(commissionValue);

//     // 1️⃣ Sort ASC first (for correct running balance calculation)
//     combinedDetails.sort(
//             Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
//     );

//     // 2️⃣ Running balance calculation
//     double balance = 0;
//     for (RetailerDetailsDTO dto : combinedDetails) {
//         double productVal = dto.getProductValue() != null ? dto.getProductValue() : 0;
//         double paymentVal = dto.getPayment() != null ? dto.getPayment() : 0;
//         double commissionVal = dto.getCommission() != null ? dto.getCommission() : 0;

//         balance += productVal;
//         balance -= paymentVal;
//         balance -= commissionVal;

//         dto.setBalance(balance);
//     }

//     // 3️⃣ Reverse to DESC (latest first)
//     Collections.reverse(combinedDetails);

//     // 4️⃣ Manual pagination
//     int start = Math.min(page * size, combinedDetails.size());
//     int end = Math.min(start + size, combinedDetails.size());
//     List<RetailerDetailsDTO> paginatedList = combinedDetails.subList(start, end);

//     return new PageImpl<>(paginatedList, PageRequest.of(page, size), combinedDetails.size());
// }


    public List<RetailerDetailsDTO> getDetailsByRetailerAndSalesPerson(
            String retailerName, String salesPerson) {
        ClosingSetup lastClosingSetup = closingSetupRepository.findLastClosingSetup();

        if (lastClosingSetup != null) {
            LocalDate startDate = lastClosingSetup.getStartDate();
            LocalDate endDate = lastClosingSetup.getEndDate();

            List<RetailerDetailsDTO> productValue = Optional.ofNullable(
                    productStockrepository.findProductDetailsBySalesPersonAndRetailerName(salesPerson, retailerName,
                            startDate, endDate))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
                    retailerPaymentRepository.findPaymentDetailsBySalesPersonAndRetailerName(salesPerson, retailerName,
                            startDate, endDate))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
                    retailerCommissionRepository.findCommissionDetailsBySalesPersonAndRetailerName(salesPerson,
                            retailerName, startDate, endDate))
                    .orElse(Collections.emptyList());

            List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
            combinedDetails.addAll(productValue);
            combinedDetails.addAll(paymentValue);
            combinedDetails.addAll(commissionValue);

            combinedDetails.sort(
                    Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

            return combinedDetails;
        }

        return Collections.emptyList();
    }

    public List<RetailerDetailsDTO> getSalesDatewiseDetailsByRetailerAndSalesPerson(
            String salesPerson, String retailerName, LocalDate startDate, LocalDate endDate) {

        List<RetailerDetailsDTO> productValue = Optional.ofNullable(
                productStockrepository.findProductDetailsBySalesPersonAndRetailerName(salesPerson, retailerName,
                        startDate, endDate))
                .orElse(Collections.emptyList());

        List<RetailerDetailsDTO> paymentValue = Optional.ofNullable(
                retailerPaymentRepository.findPaymentDetailsBySalesPersonAndRetailerName(salesPerson, retailerName,
                        startDate, endDate))
                .orElse(Collections.emptyList());

        List<RetailerDetailsDTO> commissionValue = Optional.ofNullable(
                retailerCommissionRepository.findCommissionDetailsBySalesPersonAndRetailerName(salesPerson,
                        retailerName, startDate, endDate))
                .orElse(Collections.emptyList());

        List<RetailerDetailsDTO> combinedDetails = new ArrayList<>();
        combinedDetails.addAll(productValue);
        combinedDetails.addAll(paymentValue);
        combinedDetails.addAll(commissionValue);

        combinedDetails.sort(
                Comparator.comparing(RetailerDetailsDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder())));

        return combinedDetails;

    }

    public RetailerInfo updateRetailerInfo(Long id, RetailerInfo updatedRetailerInfo) {
        Optional<RetailerInfo> existingRetailerOpt = retailerInfoRepository.findById(id);

        if (existingRetailerOpt.isPresent()) {
            RetailerInfo existingRetailer = existingRetailerOpt.get();

            // Check if the updated retailer name already exists (excluding the current
            // retailer)
            boolean retailerNameExists = retailerInfoRepository.existsByRetailerNameAndIdNot(
                    updatedRetailerInfo.getRetailerName(), id);

            if (retailerNameExists) {

                throw new RuntimeException("Retailer name '" + updatedRetailerInfo.getRetailerName()
                        + "' already exists. Update failed.");
            }

            // Update retailer info
            existingRetailer.setCategory(updatedRetailerInfo.getCategory());
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

}
