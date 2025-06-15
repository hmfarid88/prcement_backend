package com.example.bake_boss_backend.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bake_boss_backend.dto.EmployeeMonthlySummary;
import com.example.bake_boss_backend.dto.NetSumAmountDto;
import com.example.bake_boss_backend.dto.PaymentDto;
import com.example.bake_boss_backend.dto.ReceiveDto;
import com.example.bake_boss_backend.entity.EmployeePayment;
import com.example.bake_boss_backend.entity.Expense;
import com.example.bake_boss_backend.entity.OfficeReceive;
import com.example.bake_boss_backend.entity.RetailerCommission;
import com.example.bake_boss_backend.entity.OfficePayment;
import com.example.bake_boss_backend.entity.RetailerPayment;
import com.example.bake_boss_backend.entity.SupplierCommission;
import com.example.bake_boss_backend.entity.SupplierPayment;
import com.example.bake_boss_backend.entity.TransportPayment;
import com.example.bake_boss_backend.repository.EmployeePaymentRepository;
import com.example.bake_boss_backend.repository.ExpenseRepository;
import com.example.bake_boss_backend.repository.OfficeReceiveRepository;
import com.example.bake_boss_backend.repository.RetailerCommissionRepository;
import com.example.bake_boss_backend.repository.OfficePaymentRepository;
import com.example.bake_boss_backend.repository.RetailerPaymentRepository;
import com.example.bake_boss_backend.repository.SupplierCommissionRepository;
import com.example.bake_boss_backend.repository.SupplierPaymentRepository;
import com.example.bake_boss_backend.repository.TransportPaymentRepository;
import com.example.bake_boss_backend.service.EmployeeTargetService;
import com.example.bake_boss_backend.service.ExpenseService;
import com.example.bake_boss_backend.service.OfficePaymentService;
import com.example.bake_boss_backend.service.ReceiveService;
import com.example.bake_boss_backend.service.RetailerPaymentService;
import com.example.bake_boss_backend.service.SupplierPaymentService;
import com.example.bake_boss_backend.service.TransportPaymentService;

@RestController
@RequestMapping("/paymentApi")
public class TransactionController {

    @Autowired
    private OfficePaymentService officePaymentService;

    @Autowired
    private SupplierPaymentService supplierPaymentService;

    @Autowired
    private ReceiveService receiveService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private RetailerPaymentService retailerPaymentService;

    @Autowired
    private EmployeeTargetService employeeTargetService;

    @Autowired
    private TransportPaymentService transportPaymentService;

    private final OfficeReceiveRepository officeReceiveRepository;
    private final OfficePaymentRepository officePaymentRepository;
    private final RetailerPaymentRepository retailerPaymentRepository;
    private final SupplierPaymentRepository supplierPaymentRepository;
    private final ExpenseRepository expenseRepository;
    private final EmployeePaymentRepository employeePaymentRepository;
    private final SupplierCommissionRepository supplierCommissionRepository;
    private final RetailerCommissionRepository retailerCommissionRepository;
    private final TransportPaymentRepository transportPaymentRepository;

    TransactionController(OfficeReceiveRepository officeReceiveRepository,
            OfficePaymentRepository paymentRecordRepository,
            RetailerPaymentRepository retailerPaymentRepository,
            SupplierPaymentRepository supplierPaymentRepository,
            ExpenseRepository expenseRepository,
            EmployeePaymentRepository employeePaymentRepository,
            SupplierCommissionRepository supplierCommissionRepository,
            RetailerCommissionRepository retailerCommissionRepository,
            TransportPaymentRepository transportPaymentRepository) {
        this.officeReceiveRepository = officeReceiveRepository;
        this.officePaymentRepository = paymentRecordRepository;
        this.retailerPaymentRepository = retailerPaymentRepository;
        this.supplierPaymentRepository = supplierPaymentRepository;
        this.expenseRepository = expenseRepository;
        this.employeePaymentRepository = employeePaymentRepository;
        this.supplierCommissionRepository = supplierCommissionRepository;
        this.retailerCommissionRepository = retailerCommissionRepository;
        this.transportPaymentRepository = transportPaymentRepository;
    }

    @PostMapping("/officeReceive")
    public OfficeReceive newItem(@RequestBody OfficeReceive officeReceive) {
        return officeReceiveRepository.save(officeReceive);
    }

    @PostMapping("/officePayment")
    public OfficePayment newItem(@RequestBody OfficePayment paymentRecord) {
        return officePaymentRepository.save(paymentRecord);
    }

    @PostMapping("/expenseRecord")
    public Expense expenseItem(@RequestBody Expense expenseRecord) {
        return expenseRepository.save(expenseRecord);
    }

    @PostMapping("/retailerPayment")
    public RetailerPayment newItem(@RequestBody RetailerPayment retailerPayment) {
        return retailerPaymentRepository.save(retailerPayment);
    }

    @PostMapping("/supplierCommission")
    public SupplierCommission newItem(@RequestBody SupplierCommission supplierCommission) {
        return supplierCommissionRepository.save(supplierCommission);
    }

    @PostMapping("/retailerCommission")
    public RetailerCommission newItem(@RequestBody RetailerCommission retailerCommission) {
        return retailerCommissionRepository.save(retailerCommission);
    }

    @PostMapping("/supplierPayment")
    public SupplierPayment newItem(@RequestBody SupplierPayment supplierPayment) {
        return supplierPaymentRepository.save(supplierPayment);
    }

    @PostMapping("/employeePayment")
    public EmployeePayment newItem(@RequestBody EmployeePayment employeePayment) {
        return employeePaymentRepository.save(employeePayment);
    }

    @PostMapping("/transportPayment")
    public TransportPayment newItem(@RequestBody TransportPayment transportPayment) {
        return transportPaymentRepository.save(transportPayment);
    }

    @GetMapping("/net-sum-before-today")
    public NetSumAmountDto getNetSumAmountBeforeToday(@RequestParam String username, LocalDate date) {
        return officePaymentService.getNetSumAmountBeforeToday(username, date);
    }

    @GetMapping("/payments/today")
    public List<PaymentDto> getPaymentsForToday(@RequestParam String username, LocalDate date) {
        return officePaymentService.getPaymentsForToday(username, date);
    }

    @GetMapping("/receives/today")
    public List<ReceiveDto> getReceivesForToday(@RequestParam String username, LocalDate date) {
        return receiveService.findReceivesForToday(username, date);
    }

    @GetMapping("/getExpense")
    public List<Expense> getCurrentMonthExpenses(@RequestParam String username) {
        return expenseService.getCurrentMonthExpenses(username);
    }

    @GetMapping("/getDatewiseExpense")
    public List<Expense> getDatewiseExpenses(@RequestParam String username, LocalDate startDate, LocalDate endDate) {
        return expenseService.getDatewiseExpenses(username, startDate, endDate);
    }

    @GetMapping("/getOfficePay")
    public List<OfficePayment> getPaymentsForCurrentMonth(@RequestParam String username) {
        return officePaymentService.getPaymentsForCurrentMonth(username);
    }

    @GetMapping("/getDatewiseOfficePay")
    public List<OfficePayment> getDatewiseOfficePay(@RequestParam String username, LocalDate startDate,
            LocalDate endDate) {
        return officePaymentService.getDatewiseOfficePay(username, startDate, endDate);
    }

    @GetMapping("/getSupplierPay")
    public List<SupplierPayment> getsupplierForCurrentMonth(@RequestParam String username) {
        return supplierPaymentService.getSupplierForCurrentMonth(username);
    }


    @GetMapping("/getDatewiseSupplierPayment")
    public List<SupplierPayment> getDatewiseSupplierPayment(@RequestParam String username, LocalDate startDate, LocalDate endDate) {
        return supplierPaymentService.getDatewiseSupplierPayment(username, startDate, endDate);
    }

    
    @GetMapping("/getTransportPay")
    public List<TransportPayment> getTransportPayForCurrentMonth(@RequestParam String username) {
        return transportPaymentService.getTransportPayForCurrentMonth(username);
    }

    @GetMapping("/getDatewiseTransportPay")
    public List<TransportPayment> getDatewiseTransportPayment(@RequestParam String username, LocalDate startDate, LocalDate endDate) {
        return transportPaymentService.getDatewiseTransportPayment(username, startDate, endDate);
    }

    @GetMapping("/getOfficeReceive")
    public List<OfficeReceive> getReceiveForCurrentMonth(@RequestParam String username) {
        return receiveService.getReceivesForCurrentMonth(username);
    }

    @GetMapping("/getDatewiseOfficeReceive")
    public List<OfficeReceive> getDatewiseReceive(@RequestParam String username, LocalDate startDate,
            LocalDate endDate) {
        return receiveService.getDatewiseOfficeReceive(username, startDate, endDate);
    }

    @GetMapping("/getRetailerPayment")
    public List<RetailerPayment> getRetailerPayForCurrentMonth(@RequestParam String username) {
        return retailerPaymentService.getRetailerPayForCurrentMonth(username);
    }

    @GetMapping("/getDatewiseRetailerPayment")
    public List<RetailerPayment> getDatewiseRetailerPayForCurrentMonth(@RequestParam String username,
            LocalDate startDate, LocalDate endDate) {
        return retailerPaymentService.getDatewiseRetailerPay(username, startDate, endDate);
    }

    @GetMapping("/getEmployeePayment")
    public List<EmployeePayment> getEmployeePayForCurrentMonth(@RequestParam String username) {
        return retailerPaymentService.getEmployeePayForCurrentMonth(username);
    }

    @GetMapping("/getDatewiseEmployeePayment")
    public List<EmployeePayment> getDatewiseEmployeePayment(@RequestParam String username, LocalDate startDate,
            LocalDate endDate) {
        return retailerPaymentService.getDatewiseEmployeePay(username, startDate, endDate);
    }

    @GetMapping("/getRetailerCommission")
    public List<RetailerCommission> getRetailerCommissionForCurrentMonth(@RequestParam String username) {
        return retailerPaymentService.getRetailerCommissionForCurrentMonth(username);
    }

    @GetMapping("/getDatewiseRetailerCommission")
    public List<RetailerCommission> getDatewiseRetailerCommission(@RequestParam String username, LocalDate startDate,
            LocalDate endDate) {
        return retailerPaymentService.getDatewiseRetailerCommission(username, startDate, endDate);
    }

    @GetMapping("/getSupplierCommission")
    public List<SupplierCommission> getSupplierCommissionForCurrentMonth(@RequestParam String username) {
        return supplierPaymentService.getSupplierCommissionForCurrentMonth(username);
    }

    @GetMapping("/getDatewiseSupplierCommission")
    public List<SupplierCommission> getDatewiseSupplierCommission(@RequestParam String username, LocalDate startDate,
            LocalDate endDate) {
        return supplierPaymentService.getDatewiseSupplierCommission(username, startDate, endDate);
    }

    @GetMapping("/employee-targetList")
    public List<EmployeeMonthlySummary> getEmployeeMonthlySummary() {
        return employeeTargetService.getAllEmployeeMonthlySummaries();
    }

    @GetMapping("/singleEmployee-targetList")
    public List<EmployeeMonthlySummary> getEmployeeTargetSummary(@RequestParam String employeeName) {
        return employeeTargetService.getEmployeeMonthlySummaries(employeeName);
    }

    @GetMapping("/employee-total-taken")
    public Double getSumOfAmountByEmployeeNameYearMonthAndUsername(String username, String employeeName, int year, int month) {
        return employeePaymentRepository.findSumOfAmountByEmployeeNameYearMonthAndUsername(username, employeeName, year, month);
    }

    @GetMapping("/getExpenseInfo/{id}")
    public ResponseEntity<?> getExpenseById(@PathVariable Long id) {
        try {
            Optional<Expense> expense = expenseService.getExpenseById(id);
            return ResponseEntity.ok(expense);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getOfficepaymentInfo/{id}")
    public ResponseEntity<?> getOfficepayById(@PathVariable Long id) {
        try {
            Optional<OfficePayment> officepay = officePaymentService.getOfficepayById(id);
            return ResponseEntity.ok(officepay);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getEmployeepaymentInfo/{id}")
    public ResponseEntity<?> getEmployeepayById(@PathVariable Long id) {
        try {
            Optional<EmployeePayment> officepay = officePaymentService.getEmployeepayById(id);
            return ResponseEntity.ok(officepay);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getSupplierpaymentInfo/{id}")
    public ResponseEntity<?> getSupplierpayById(@PathVariable Long id) {
        try {
            Optional<SupplierPayment> officepay = supplierPaymentService.getSupplierpayById(id);
            return ResponseEntity.ok(officepay);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getTransportPayInfo/{id}")
    public ResponseEntity<?> getTransportpayById(@PathVariable Long id) {
        try {
            Optional<TransportPayment> officepay = transportPaymentService.getTransportPayById(id);
            return ResponseEntity.ok(officepay);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getRetailerCommissionInfo/{id}")
    public ResponseEntity<?> getRetailercommissionById(@PathVariable Long id) {
        try {
            Optional<RetailerCommission> officepay = retailerPaymentService.getRetailerCommissionById(id);
            return ResponseEntity.ok(officepay);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getOfficeReceiveInfo/{id}")
    public ResponseEntity<?> getOfficeReceiveById(@PathVariable Long id) {
        try {
            Optional<OfficeReceive> officepay = officePaymentService.getOfficeReceiveById(id);
            return ResponseEntity.ok(officepay);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getSupplierCommissionInfo/{id}")
    public ResponseEntity<?> getSuppliercommissionById(@PathVariable Long id) {
        try {
            Optional<SupplierCommission> officepay = supplierPaymentService.getSupplierCommissionById(id);
            return ResponseEntity.ok(officepay);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/updateExpenseInfo/{id}")
    public ResponseEntity<?> updateExpenseInfo(@PathVariable Long id, @RequestBody Expense expenseInfo) {
        try {
            Expense updatedExpense = expenseService.updateExpenseInfo(id, expenseInfo);
            return ResponseEntity.ok(updatedExpense);
        } catch (RuntimeException e) {
            // Return a response with the error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PutMapping("/updateOfficepayInfo/{id}")
    public ResponseEntity<?> updateOfficepayInfo(@PathVariable Long id, @RequestBody OfficePayment officePayment) {
        try {
            OfficePayment updatedPayment = officePaymentService.updatePaymentInfo(id, officePayment);
            return ResponseEntity.ok(updatedPayment);
        } catch (RuntimeException e) {
            // Return a response with the error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PutMapping("/updateEmployeePayInfo/{id}")
    public ResponseEntity<?> updateEmployeepayInfo(@PathVariable Long id, @RequestBody EmployeePayment officePayment) {
        try {
            EmployeePayment updatedPayment = officePaymentService.updateEmployeePaymentInfo(id, officePayment);
            return ResponseEntity.ok(updatedPayment);
        } catch (RuntimeException e) {
            // Return a response with the error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PutMapping("/updateSupplierPayInfo/{id}")
    public ResponseEntity<?> updateSupplierpayInfo(@PathVariable Long id, @RequestBody SupplierPayment officePayment) {
        try {
            SupplierPayment updatedPayment = supplierPaymentService.updateSupplierPaymentInfo(id, officePayment);
            return ResponseEntity.ok(updatedPayment);
        } catch (RuntimeException e) {
            // Return a response with the error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PutMapping("/updateTransportPayInfo/{id}")
    public ResponseEntity<?> updateTransportPayInfo(@PathVariable Long id, @RequestBody TransportPayment officePayment) {
        try {
            TransportPayment updatedPayment = transportPaymentService.updateTransportPaymentInfo(id, officePayment);
            return ResponseEntity.ok(updatedPayment);
        } catch (RuntimeException e) {
            // Return a response with the error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PutMapping("/updateRetailerCommissionInfo/{id}")
    public ResponseEntity<?> updateRetailerCommissionInfo(@PathVariable Long id,
            @RequestBody RetailerCommission officePayment) {
        try {
            RetailerCommission updatedPayment = retailerPaymentService.updateRetailerCommission(id, officePayment);
            return ResponseEntity.ok(updatedPayment);
        } catch (RuntimeException e) {
            // Return a response with the error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PutMapping("/updateOfficeRecdeiveInfo/{id}")
    public ResponseEntity<?> updateOfficereceiveInfo(@PathVariable Long id, @RequestBody OfficeReceive officePayment) {
        try {
            OfficeReceive updatedPayment = officePaymentService.updateReceiveInfo(id, officePayment);
            return ResponseEntity.ok(updatedPayment);
        } catch (RuntimeException e) {
            // Return a response with the error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PutMapping("/updateSupplierCommissionInfo/{id}")
    public ResponseEntity<?> updateSupplierCommissionInfo(@PathVariable Long id,
            @RequestBody SupplierCommission officePayment) {
        try {
            SupplierCommission updatedPayment = supplierPaymentService.updateSupplierCommissionInfo(id, officePayment);
            return ResponseEntity.ok(updatedPayment);
        } catch (RuntimeException e) {
            // Return a response with the error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }
}
