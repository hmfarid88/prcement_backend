package com.example.bake_boss_backend.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bake_boss_backend.entity.ClosingSetup;
import com.example.bake_boss_backend.entity.EmployeeInfo;
import com.example.bake_boss_backend.entity.EmployeeTarget;
import com.example.bake_boss_backend.entity.ExpenseName;
import com.example.bake_boss_backend.entity.OrderInfo;
import com.example.bake_boss_backend.entity.PaymentName;
import com.example.bake_boss_backend.entity.ProductName;
import com.example.bake_boss_backend.entity.ProductStock;
import com.example.bake_boss_backend.entity.RetailerInfo;
import com.example.bake_boss_backend.entity.SupplierName;
import com.example.bake_boss_backend.entity.Transport;
import com.example.bake_boss_backend.entity.UserInfo;
import com.example.bake_boss_backend.repository.EmployeeInfoRepository;
import com.example.bake_boss_backend.repository.EmployeePaymentRepository;
import com.example.bake_boss_backend.repository.EmployeeTargetRepository;
import com.example.bake_boss_backend.repository.ExpenseNameRepository;
import com.example.bake_boss_backend.repository.ExpenseRepository;
import com.example.bake_boss_backend.repository.OfficePaymentRepository;
import com.example.bake_boss_backend.repository.OfficeReceiveRepository;
import com.example.bake_boss_backend.repository.OrderInfoRepository;
import com.example.bake_boss_backend.repository.PaymentNameRepository;
import com.example.bake_boss_backend.repository.ProductNameRepository;
import com.example.bake_boss_backend.repository.ProductStockrepository;
import com.example.bake_boss_backend.repository.RetailerCommissionRepository;
import com.example.bake_boss_backend.repository.RetailerInfoRepository;
import com.example.bake_boss_backend.repository.RetailerPaymentRepository;
import com.example.bake_boss_backend.repository.SupplierCommissionRepository;
import com.example.bake_boss_backend.repository.SupplierNameRepository;
import com.example.bake_boss_backend.repository.SupplierPaymentRepository;
import com.example.bake_boss_backend.repository.TransportPaymentRepository;
import com.example.bake_boss_backend.repository.TransportRepository;
import com.example.bake_boss_backend.repository.UserInfoRepository;
import com.example.bake_boss_backend.service.ProductStockService;
import com.example.bake_boss_backend.service.RetailerBalanceService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final SupplierNameRepository supplierNameRepository;
    private final ProductStockrepository productStockrepository;

    ProductController(
            SupplierNameRepository supplierNameRepository,
            ProductStockrepository productStockrepository) {
        this.supplierNameRepository = supplierNameRepository;
        this.productStockrepository = productStockrepository;

    }

    @Autowired
    private ProductStockService productStockService;

    @Autowired
    private RetailerBalanceService retailerBalanceService;

    @Autowired
    private ProductNameRepository productNameRepository;

    @Autowired
    private EmployeeInfoRepository employeeInfoRepository;

    @Autowired
    private RetailerInfoRepository retailerInfoRepository;

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Autowired
    private PaymentNameRepository paymentNameRepository;

    @Autowired
    private EmployeeTargetRepository employeeTargetRepository;

    @Autowired
    private EmployeePaymentRepository employeePaymentRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private OfficePaymentRepository officePaymentRepository;

    @Autowired
    private OfficeReceiveRepository officeReceiveRepository;

    @Autowired
    private RetailerPaymentRepository retailerPaymentRepository;

    @Autowired
    private RetailerCommissionRepository retailerCommissionRepository;

    @Autowired
    private SupplierCommissionRepository supplierCommissionRepository;

    @Autowired
    private SupplierPaymentRepository supplierPaymentRepository;

    @Autowired
    private TransportRepository transportRepository;

    @Autowired
    private TransportPaymentRepository transportPaymentRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ExpenseNameRepository expenseNameRepository;

    @PostMapping("/closingSetup")
    public ClosingSetup saveOrUpdateClosingSetup(@RequestBody Map<String, String> request) {
        LocalDate startDate = LocalDate.parse(request.get("startDate"));
        LocalDate endDate = LocalDate.parse(request.get("endDate"));
        return productStockService.saveOrUpdateClosingSetup(startDate, endDate);
    }

    @PostMapping("/addProductName")
    public ResponseEntity<?> addProduct(@RequestBody ProductName productName) {
        if (productNameRepository.existsByUsernameAndProductName(productName.getUsername(),
                productName.getProductName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Product " + productName.getProductName() + " is already exists!");
        }
        ProductName savedProduct = productNameRepository.save(productName);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PostMapping("/addExpenseName")
    public ResponseEntity<?> addExpense(@RequestBody ExpenseName expenseName) {
        if (expenseNameRepository.existsByUsernameAndExpenseName(expenseName.getUsername(), expenseName.getExpenseName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Product " + expenseName.getExpenseName() + " is already exists!");
        }
        ExpenseName savedExpenseName = expenseNameRepository.save(expenseName);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpenseName);
    }

    @PostMapping("/addTransport")
    public ResponseEntity<?> addTransport(@RequestBody Transport transport) {
        if (transportRepository.existsByUsernameAndTransport(transport.getUsername(),
                transport.getTransport())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Transport " + transport.getTransport() + " is already exists!");
        }
        Transport savedTransport = transportRepository.save(transport);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransport);
    }

    @PostMapping("/addEmployeeTarget")
    public ResponseEntity<?> addOrUpdateEmployeeTarget(@RequestBody EmployeeTarget employeeTarget) {
        Optional<EmployeeTarget> existingTarget = employeeTargetRepository
                .findByEmployeeNameAndYearAndMonth(employeeTarget.getEmployeeName(), employeeTarget.getYear(),
                        employeeTarget.getMonth());
        if (existingTarget.isPresent()) {
            EmployeeTarget targetToUpdate = existingTarget.get();
            targetToUpdate.setAmount(employeeTarget.getAmount());
            targetToUpdate.setTargetName(employeeTarget.getTargetName());
            EmployeeTarget updatedTarget = employeeTargetRepository.save(targetToUpdate);
            return ResponseEntity.ok(updatedTarget);
        }

        EmployeeTarget savedTarget = employeeTargetRepository.save(employeeTarget);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTarget);
    }

    @PostMapping("/addRetailerInfo")
    public ResponseEntity<?> addRetailer(@RequestBody RetailerInfo retailerInfo) {
        if (retailerInfoRepository.existsByRetailerName(retailerInfo.getRetailerName())) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Retailer " + retailerInfo.getRetailerName() + " is already exists!");
        }
        RetailerInfo savedRetailer = retailerInfoRepository.save(retailerInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRetailer);
    }

    @PutMapping("/updateRetailerInfo/{id}")
    public ResponseEntity<?> updateRetailerInfo(@PathVariable Long id, @RequestBody RetailerInfo retailerInfo) {
        try {
            RetailerInfo updatedRetailer = retailerBalanceService.updateRetailerInfo(id, retailerInfo);
            return ResponseEntity.ok(updatedRetailer);
        } catch (RuntimeException e) {
            // Return a response with the error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @PutMapping("/updateEmployeeInfo/{id}")
    public ResponseEntity<?> updateEmployeeInfo(@PathVariable Long id, @RequestBody EmployeeInfo employeeInfo) {
        try {
            EmployeeInfo updatedEmployee = retailerBalanceService.updateEmployeeInfo(id, employeeInfo);
            return ResponseEntity.ok(updatedEmployee);
        } catch (RuntimeException e) {
            // Return a response with the error message
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @DeleteMapping("/deleteEmployeeById/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable Long id) {
        Optional<EmployeeInfo> employeeOpt = employeeInfoRepository.findById(id);

        if (employeeOpt.isPresent()) {
            EmployeeInfo employee = employeeOpt.get();
            String employeeName = employee.getEmployeeName(); // same as username

            // Delete the employee record
            employeeInfoRepository.deleteById(id);

            // Find and delete the corresponding UserInfo if role is "role_sales"
            UserInfo userInfo = userInfoRepository.findByUsername(employeeName);
            if (userInfo != null && "ROLE_SALES".equalsIgnoreCase(userInfo.getRoles())) {
                userInfoRepository.delete(userInfo);
            }

            return ResponseEntity.ok("Employee and related user deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found.");
        }
    }

    @DeleteMapping("/deleteRetailerById/{id}")
    public ResponseEntity<String> deleteRetailerById(@PathVariable Long id) {
        Optional<RetailerInfo> retailerOpt = retailerInfoRepository.findById(id);

        if (retailerOpt.isPresent()) {
            retailerInfoRepository.deleteById(id);
            return ResponseEntity.ok("Retailer deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Retailer not found.");
        }
    }

    @Transactional
    @DeleteMapping("/deletePaymentName")
    public ResponseEntity<String> deletePaymentName(@RequestParam String paymentPerson) {
        paymentNameRepository.deleteByPaymentPerson(paymentPerson);
        return ResponseEntity.ok("Name deleted successfully.");
    }

    @Transactional
    @DeleteMapping("/deleteSupplierName")
    public ResponseEntity<String> deleteSupplierName(@RequestParam String supplierName) {
        supplierNameRepository.deleteBySupplierName(supplierName);
        return ResponseEntity.ok("Name deleted successfully.");

    }

    @Transactional
    @DeleteMapping("/deleteProductName")
    public ResponseEntity<String> deleteProductName(@RequestParam String productName) {
        productNameRepository.deleteByProductName(productName);
        return ResponseEntity.ok("Name deleted successfully.");
    }

    @Transactional
    @DeleteMapping("/deleteExpenseName")
    public ResponseEntity<String> deleteExpenseName(@RequestParam String expenseName) {
        expenseNameRepository.deleteByExpenseName(expenseName);
        return ResponseEntity.ok("Name deleted successfully.");
    }

    @Transactional
    @DeleteMapping("/deleteTransportName")
    public ResponseEntity<String> deleteTransportName(@RequestParam String transport) {
        transportRepository.deleteByTransport(transport);
        return ResponseEntity.ok("Name deleted successfully.");

    }

    @PutMapping("/updateSaleInfo/{productId}")
    public ResponseEntity<?> updateProductInfo(@PathVariable Long productId, @RequestBody ProductStock productstock) {
        try {
            ProductStock updatedProduct = productStockService.updateProductSale(productId, productstock);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }
   @DeleteMapping("/deleteProductSale/{productId}")
    public ResponseEntity<String> deleteProductSale(@PathVariable Long productId) {
        try {
            productStockService.deleteProductSale(productId);
            return ResponseEntity.ok("Product sale deleted successfully with ID: " + productId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PutMapping("/updateEntryInfo/{productId}")
    public ResponseEntity<?> updateEntryInfo(@PathVariable Long productId, @RequestBody ProductStock productstock) {
        try {
            ProductStock updatedProduct = productStockService.updateProductEntry(productId, productstock);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @DeleteMapping("/deleteEntryInfo/{productId}")
    public ResponseEntity<?> deleteEntryInfo(@PathVariable Long productId) {
        try {
            ProductStock deletedProduct = productStockService.deleteProductEntry(productId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product deleted successfully");
            response.put("deletedProduct", deletedProduct);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(400).body(errorResponse);
        }
    }

    @DeleteMapping("/deleteEmpayById/{id}")
    public ResponseEntity<String> deleteEmployeePayById(@PathVariable Long id) {
        if (employeePaymentRepository.existsById(id)) {
            employeePaymentRepository.deleteById(id);
            return ResponseEntity.ok("Payment deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found.");
        }
    }

    @DeleteMapping("/deleteExpenseById/{id}")
    public ResponseEntity<String> deleteExpenseById(@PathVariable Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return ResponseEntity.ok("Payment deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found.");
        }
    }

    @DeleteMapping("/deleteOfficePayById/{id}")
    public ResponseEntity<String> deleteOfficePayById(@PathVariable Long id) {
        if (officePaymentRepository.existsById(id)) {
            officePaymentRepository.deleteById(id);
            return ResponseEntity.ok("Payment deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found.");
        }
    }

    @DeleteMapping("/deleteReceiveById/{id}")
    public ResponseEntity<String> deleteReceiveById(@PathVariable Long id) {
        if (officeReceiveRepository.existsById(id)) {
            officeReceiveRepository.deleteById(id);
            return ResponseEntity.ok("Payment deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found.");
        }
    }

    @DeleteMapping("/deleteRetailerPaymentById/{id}")
    public ResponseEntity<String> deletePaymentById(@PathVariable Long id) {
        if (retailerPaymentRepository.existsById(id)) {
            retailerPaymentRepository.deleteById(id);
            return ResponseEntity.ok("Payment deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found.");
        }
    }

    @DeleteMapping("/deleteRetailerComById/{id}")
    public ResponseEntity<String> deleteComById(@PathVariable Long id) {
        if (retailerCommissionRepository.existsById(id)) {
            retailerCommissionRepository.deleteById(id);
            return ResponseEntity.ok("Payment deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found.");
        }
    }

    @DeleteMapping("/deleteSupplierComById/{id}")
    public ResponseEntity<String> deleteSuppComById(@PathVariable Long id) {
        if (supplierCommissionRepository.existsById(id)) {
            supplierCommissionRepository.deleteById(id);
            return ResponseEntity.ok("Payment deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found.");
        }
    }

    @DeleteMapping("/deleteSupplierPayById/{id}")
    public ResponseEntity<String> deleteSuppPayById(@PathVariable Long id) {
        if (supplierPaymentRepository.existsById(id)) {
            supplierPaymentRepository.deleteById(id);
            return ResponseEntity.ok("Payment deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found.");
        }
    }

    @DeleteMapping("/deleteTransportPayById/{id}")
    public ResponseEntity<String> deleteTransportPayById(@PathVariable Long id) {
        if (transportPaymentRepository.existsById(id)) {
            transportPaymentRepository.deleteById(id);
            return ResponseEntity.ok("Payment deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found.");
        }
    }

    @PostMapping("/addEmployeeInfo")
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeInfo employeeInfo) {
        if (employeeInfoRepository.existsByEmployeeName(employeeInfo.getEmployeeName())) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Employee " + employeeInfo.getEmployeeName() + " is already exists!");
        }
        EmployeeInfo savedEmployee = employeeInfoRepository.save(employeeInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
    }

    @PostMapping("/addSupplierName")
    public ResponseEntity<?> addSupplier(@RequestBody SupplierName supplierName) {
        if (supplierNameRepository.existsByUsernameAndSupplierName(supplierName.getUsername(),
                supplierName.getSupplierName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Materials " + supplierName.getSupplierName() + " is already exists!");
        }
        SupplierName savedSupplier = supplierNameRepository.save(supplierName);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSupplier);
    }

    @PostMapping("/addPaymentName")
    public ResponseEntity<?> addPaymentName(@RequestBody PaymentName paymentName) {
        if (paymentNameRepository.existsByUsernameAndPaymentPerson(paymentName.getUsername(),
                paymentName.getPaymentPerson())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Name " + paymentName.getPaymentPerson() + " is already exists!");
        }
        PaymentName savedName = paymentNameRepository.save(paymentName);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedName);
    }

    @PostMapping("/addAllProducts")
    public List<ProductStock> saveProducts(@RequestBody List<ProductStock> allItems) {
        for (ProductStock newItem : allItems) {
            Optional<ProductStock> latestProductStockOpt = productStockrepository
                    .findTopByProductNameAndUsernameOrderByProductIdDesc(newItem.getProductName(),
                            newItem.getUsername());

            if (latestProductStockOpt.isPresent()) {
                ProductStock latestProductStock = latestProductStockOpt.get();
                Double newTotalQty = latestProductStock.getRemainingQty() + newItem.getProductQty();
                Double totalValue = (latestProductStock.getRemainingQty() * latestProductStock.getCostPrice()) +
                        (newItem.getProductQty() * newItem.getCostPrice());
                Double newCostPrice = totalValue / newTotalQty;
                newItem.setRemainingQty(latestProductStock.getRemainingQty() + newItem.getProductQty());
                newItem.setCostPrice(newCostPrice);

            } else {
                newItem.setRemainingQty(newItem.getProductQty());
                newItem.setCostPrice(newItem.getCostPrice());

            }
            productStockrepository.save(newItem);
        }
        return allItems;
    }

    @PostMapping("/addAllOrders")
    public List<OrderInfo> saveMultipleOrders(@RequestBody List<OrderInfo> orderInfoList) {
        return orderInfoRepository.saveAll(orderInfoList);
    }

    @PostMapping("/productDistribution")
    public List<ProductStock> saveDistribution(@RequestBody List<ProductStock> allItems) {
        for (ProductStock newItem : allItems) {
            // Update the ProductStock as you are already doing
            Optional<ProductStock> latestProductStockOpt = productStockrepository
                    .findTopByProductNameAndUsernameOrderByProductIdDesc(newItem.getProductName(),
                            newItem.getUsername());

            if (latestProductStockOpt.isPresent()) {
                ProductStock latestProductStock = latestProductStockOpt.get();
                newItem.setCostPrice(latestProductStock.getCostPrice());
                newItem.setRemainingQty(latestProductStock.getRemainingQty() - newItem.getProductQty());
                newItem.setPurchasePrice(latestProductStock.getPurchasePrice());
                newItem.setSupplier(latestProductStock.getSupplier());
            }
            productStockrepository.save(newItem);

            // Now update the deliveredQty in the OrderInfo entity
            Long orderId = newItem.getOrderId(); // Assuming orderId is available in ProductStock
            Optional<OrderInfo> orderInfoOpt = orderInfoRepository.findById(orderId);
            if (orderInfoOpt.isPresent()) {
                OrderInfo orderInfo = orderInfoOpt.get();
                orderInfo.setDeliveredQty(orderInfo.getDeliveredQty() + newItem.getProductQty());
                orderInfoRepository.save(orderInfo);
            }
        }
        return allItems;
    }

    @GetMapping("/getRetailerInfo")
    public List<RetailerInfo> getRetailer() {
        return retailerInfoRepository.findAllByOrderByRetailerNameAsc();
    }

    @GetMapping("/getRetailerInfoByRetailer")
    public RetailerInfo getRetailerInfo(@RequestParam String retailerName) {
        return retailerInfoRepository.findByRetailerName(retailerName);
    }

    @GetMapping("/getEmployeeInfoByEmployee")
    public EmployeeInfo getEmployeeInfo(@RequestParam String employeeName) {
        return employeeInfoRepository.findByEmployeeName(employeeName);
    }

    @GetMapping("/getSalesRetailerInfo")
    public List<RetailerInfo> getSalesRetailer(@RequestParam String salesPerson) {
        return retailerInfoRepository.findBySalesPerson(salesPerson);
    }

    @GetMapping("/getEmployeeInfo")
    public List<EmployeeInfo> getEmployee() {
        return employeeInfoRepository.findAll();
    }

    @GetMapping("/getSuppliersName")
    public List<SupplierName> getSupplierNameByUsername(@RequestParam String username) {
        return supplierNameRepository.getSupplierNameByUsername(username);
    }

    @GetMapping("/getTransport")
    public List<Transport> getTransport(@RequestParam String username) {
        return transportRepository.getTransportByUsername(username);
    }

    @GetMapping("/getPaymentPerson")
    public List<PaymentName> getPaymentNameByUsername(@RequestParam String username) {
        return paymentNameRepository.getPaymentPersonByUsername(username);
    }

    @GetMapping("/getProductName")
    public List<ProductName> getProductNameByUsername() {
        return productNameRepository.findAll();
    }

    @GetMapping("/getExpenseName")
    public List<ExpenseName> getProductNameByUsername(@RequestParam String username) {
        return expenseNameRepository.findByUsername(username);
    }

    @GetMapping("/getProductStock")
    public List<ProductStock> getLatestProductStockForEachProductName(String username) {
        return productStockrepository.findLatestProductStockForEachProductName(username);
    }

    @GetMapping("/getSoldProduct")
    public List<ProductStock> getSoldProduct(String username) {
        return productStockService.getProductDistForCurrentMonth(username);
    }

    @GetMapping("/getDatewiseSoldProduct")
    public List<ProductStock> getDatewiseSoldProduct(String username, LocalDate startDate, LocalDate endDate) {
        return productStockService.getDatewiseSoldProductStock(username, startDate, endDate);
    }

    @GetMapping("/getSalesPersonSoldProduct")
    public List<ProductStock> getSalesSoldProduct(String username) {
        return productStockService.getSalesProductDistForCurrentMonth(username);
    }

    @GetMapping("/getSalesPersonDatewiseSoldProduct")
    public List<ProductStock> getSalesDatewiseSoldProduct(String username, LocalDate startDate, LocalDate endDate) {
        return productStockService.getSalesDatewiseSoldProduct(username, startDate, endDate);
    }

    @GetMapping("/getAllProduct")
    public List<ProductStock> getAllProduct(String username) {
        return productStockService.getAllProductStock(username);
    }

    @GetMapping("/datewise-stock-ledger")
    public List<ProductStock> getDatewiseAllProduct(String username, LocalDate startDate, LocalDate endDate) {
        return productStockService.getDatewiseProductStock(username, startDate, endDate);
    }

    @GetMapping("/getInvoiceData")
    public List<ProductStock> getInvoiceData(String username, String invoiceNo) {
        return productStockrepository.findByUsernameAndInvoiceNo(username, invoiceNo);
    }

    @GetMapping("/getSingleProduct")
    public Optional<ProductStock> getSingleProduct(@RequestParam Long productId) {
        return productStockrepository.findByProductId(productId);
    }

    @GetMapping("/getExistingAllOrder")
    public List<Object[]> getExistingOrder() {
        return productStockService.getExistingOrder();
    }

    @GetMapping("/getExistingSingleOrder")
    public List<OrderInfo> getExistingSingleOrder(Long orderId) {
        return productStockService.getExistingSingleOrder(orderId);
    }

    @GetMapping("/getPendingOrder")
    public List<OrderInfo> getPendingOrdersByUsername(String username) {
        return orderInfoRepository.findByUsernameAndPendingQuantity(username);
    }

    @GetMapping("/getTotalSoldToday")
    public Double getTotalSoldToday() {
        return productStockService.getTotalSoldProductQtyToday();
    }

    @GetMapping("findLastQty")
    public Double getLastRemainingQty(String username, String productName) {
        return productStockrepository.findLastRemainingQtyByUsernameAndProductName(username, productName);
    }

    @GetMapping("/getSaleInfo/{productId}")
    public ResponseEntity<?> getProductStockById(@PathVariable Long productId) {
        try {
            ProductStock productStock = productStockService.getProductStockById(productId);
            return ResponseEntity.ok(productStock);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/getProductEntry/{productId}")
    public ResponseEntity<?> getProductEntryById(@PathVariable Long productId) {
        try {
            ProductStock productStock = productStockService.getProductStockById(productId);
            return ResponseEntity.ok(productStock);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}
