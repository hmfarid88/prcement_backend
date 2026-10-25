package com.example.bake_boss_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bake_boss_backend.dto.DailyWarehouseStockDTO;
import com.example.bake_boss_backend.dto.MonthlySaleDTO;
import com.example.bake_boss_backend.dto.MonthlySrSaleDTO;
import com.example.bake_boss_backend.dto.ProductRetailerDTO;
import com.example.bake_boss_backend.dto.StockLedgerDTO;
import com.example.bake_boss_backend.dto.TransferStockRequest;
import com.example.bake_boss_backend.entity.OrderInfo;
import com.example.bake_boss_backend.entity.ProductStock;
import com.example.bake_boss_backend.repository.OrderInfoRepository;
import com.example.bake_boss_backend.repository.ProductStockrepository;

@Service
public class ProductStockService {
    @Autowired
    private ProductStockrepository productStockRepository;

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    public List<ProductRetailerDTO> getProductDistForCurrentMonth(String username) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return productStockRepository.findProductWithRetailerDetails(year, month, username);
    }

    public List<ProductStock> getSalesProductDistForCurrentMonth(String username) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return productStockRepository.findProductBySalesPerson(year, month, username);
    }

    public List<ProductRetailerDTO> getDatewiseSoldProductStock(String username, LocalDate startDate,
            LocalDate endDate) {
        return productStockRepository.findDatewiseSoldProductByUsername(startDate, endDate, username);
    }

    public List<ProductStock> getSalesDatewiseSoldProduct(String username, LocalDate startDate, LocalDate endDate) {
        return productStockRepository.findDatewiseProductBySalesPerson(username, startDate, endDate);
    }

    public List<ProductStock> getAllProductStock(String username) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return productStockRepository.findProductByUsername(year, month, username);
    }

    public List<ProductStock> getDatewiseProductStock(String username, LocalDate startDate, LocalDate endDate) {
        return productStockRepository.findDatewiseProductByUsername(username, startDate, endDate);
    }

    public List<ProductStock> getMonthlyTransferProductStock(String username) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return productStockRepository.findMonthlyTransferProductStock(username, year, month);
    }

    public List<ProductStock> getDatewiseTransferProductStock(
            String username, LocalDate startDate, LocalDate endDate) {
        return productStockRepository.findDatewiseTransferProductStock(username, startDate, endDate);
    }

    public List<MonthlySaleDTO> getDatewiseMonthlySale(String username, LocalDate startDate, LocalDate endDate) {
        return productStockRepository.findMonthlySaleSummary(username, startDate, endDate);
    }

    public List<MonthlySrSaleDTO> getDatewiseMonthlySrSale(String username, LocalDate startDate, LocalDate endDate) {
        return productStockRepository.findMonthlySrSaleSummary(username, startDate, endDate);
    }

    public List<Object[]> getExistingOrder() {
        return orderInfoRepository.findAllList();
    }

    public List<OrderInfo> getExistingSingleOrder(Long orderId) {
        return orderInfoRepository.findByOrderId(orderId);
    }

   
@Transactional
public List<ProductStock> transferStock(TransferStockRequest request) {

    Double quantity = request.getTransferQty();

    if (quantity == null || !Double.isFinite(quantity) || quantity <= 0) {
        throw new IllegalArgumentException(
                "Transfer quantity must be greater than zero.");
    }
   
    String sourceWarehouse = request.getSourceWarehouse().trim();
    String sourceCategory = request.getSourceCategory().trim();
    String productName = request.getProductName().trim();
    String destinationWarehouse = request.getDestinationWarehouse().trim();
    String destinationCategory = request.getDestinationCategory().trim();
    String username = request.getUsername().trim();

    // 1. Get total source stock balance
    double sourceBalance = productStockRepository.getWarehouseProductBalance(
            username, sourceWarehouse, sourceCategory, productName);

    // 2. Get latest source record for product details
    ProductStock sourceLatest = productStockRepository
            .findTopByCategoryAndProductNameAndUsernameOrderByProductIdDesc(
                    sourceCategory, productName, username)
            .orElseThrow(() -> new IllegalArgumentException(
                    "No source product record found for pricing details."));

    // 3. Get destination's total stock balance
    double destinationBalance = productStockRepository.getWarehouseProductBalance(
            username, destinationWarehouse, destinationCategory, productName);

    // 4. Get destination's latest record, if available
    ProductStock destinationLatest = productStockRepository
            .findTopByCategoryAndProductNameAndUsernameOrderByProductIdDesc(
                    destinationCategory, productName, username)
            .orElse(null);

    LocalDate transferDate = request.getDate() == null
            ? LocalDate.now()
            : request.getDate();

    // 5. Create source deduction record
    ProductStock transferOut = new ProductStock();
    transferOut.setDate(transferDate);
    transferOut.setCategory(sourceCategory);
    transferOut.setWarehouse(sourceWarehouse);
    transferOut.setProductName(productName);
    transferOut.setUsername(username);
    transferOut.setCustomer(destinationCategory);
    transferOut.setSupplier(sourceLatest.getSupplier());
    transferOut.setPurchasePrice(sourceLatest.getPurchasePrice());
    transferOut.setCostPrice(sourceLatest.getCostPrice());
    transferOut.setDpRate(sourceLatest.getDpRate());
    transferOut.setProductQty(quantity);
    transferOut.setRemainingQty(sourceBalance - quantity);
    transferOut.setStatus("sold");
    transferOut.setNote("Transfer to " + destinationWarehouse + " - " + destinationCategory + ". ");

    // 6. Create destination entry record
    ProductStock transferIn = new ProductStock();
    transferIn.setDate(transferDate);
    transferIn.setCategory(destinationCategory);
    transferIn.setWarehouse(destinationWarehouse);
    transferIn.setProductName(productName);
    transferIn.setUsername(username);
    transferIn.setProductQty(quantity);
    transferIn.setRemainingQty(destinationBalance + quantity);
    transferIn.setStatus("stored");
    transferIn.setNote("Transfer from " + sourceWarehouse + " - " + sourceCategory + ". ");

    if (destinationLatest != null) {
        double incomingCost = sourceLatest.getCostPrice() == null
                ? 0.0 : sourceLatest.getCostPrice();

        double newCost = (incomingCost);
         
        transferIn.setCostPrice(newCost);
        transferIn.setPurchasePrice(sourceLatest.getPurchasePrice());
        // transferIn.setSupplier(sourceLatest.getSupplier());
        transferIn.setDpRate(destinationLatest.getDpRate());
    } else {
        transferIn.setCostPrice(sourceLatest.getCostPrice());
        transferIn.setPurchasePrice(sourceLatest.getPurchasePrice());
        // transferIn.setSupplier(sourceLatest.getSupplier());
        transferIn.setDpRate(sourceLatest.getDpRate());
    }

    // 7. Save both records in one transaction
    return productStockRepository.saveAll(
            List.of(transferOut, transferIn));
}
   
    public Double getTotalSoldProductQtyToday() {
        return productStockRepository.findTotalSoldProductQtyToday();
    }

    public ProductStock getProductStockById(Long productId) {
        return productStockRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("ProductStock not found with ID: " + productId));
    }

    @Transactional
    public ProductStock updateProductSale(Long productId, ProductStock updatedProductStock) {
        Optional<ProductStock> existingProductOpt = productStockRepository.findById(productId);

        if (existingProductOpt.isPresent()) {
            ProductStock existingProduct = existingProductOpt.get();
            Double oldQty = existingProduct.getProductQty();
            Double newQty = updatedProductStock.getProductQty();
            Double qtyDifference = newQty - oldQty;
            String oldProductName = existingProduct.getProductName();
            String newProductName = updatedProductStock.getProductName();

            existingProduct.setDate(updatedProductStock.getDate());
            existingProduct.setProductName(updatedProductStock.getProductName());
            existingProduct.setDpRate(updatedProductStock.getDpRate());
            existingProduct.setProductQty(updatedProductStock.getProductQty());
            existingProduct.setCustomer(updatedProductStock.getCustomer());
            existingProduct.setTransport(updatedProductStock.getTransport());
            existingProduct.setTruckNo(updatedProductStock.getTruckNo());
            existingProduct.setRent(updatedProductStock.getRent());
            existingProduct.setNote(updatedProductStock.getNote());

            if (!oldProductName.equals(newProductName)) {

                Double previousRemainingQty = productStockRepository.findRemainingQtyByProductName(newProductName,
                        productId);
                Double newRemainingQty = previousRemainingQty;
                productStockRepository.updateRemainingQtyByProductName(newProductName, productId, newRemainingQty);
                productStockRepository.increaseRemainingQty(oldProductName, productId, oldQty);
                productStockRepository.reduceRemainingQty(newProductName, productId, newQty);
            }

            if (qtyDifference > 0) {
                productStockRepository.reduceRemainingQty(existingProduct.getProductName(), productId, qtyDifference);
            } else if (qtyDifference < 0) {
                productStockRepository.increaseRemainingQty(existingProduct.getProductName(), productId,
                        Math.abs(qtyDifference));
            }

            return productStockRepository.save(existingProduct);
        } else {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
    }

    @Transactional
    public void deleteProductSale(Long productId) {
        Optional<ProductStock> existingProductOpt = productStockRepository.findById(productId);

        if (existingProductOpt.isPresent()) {
            ProductStock existingProduct = existingProductOpt.get();

            String productName = existingProduct.getProductName();
            Double soldQty = existingProduct.getProductQty();

            // Restore the stock (increase remaining quantity)
            productStockRepository.increaseRemainingQty(productName, productId, soldQty);

            // Now delete the sale record
            productStockRepository.deleteById(productId);

        } else {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
    }

    @Transactional
    public ProductStock updateProductEntry(Long productId, ProductStock updatedProductStock) {
        Optional<ProductStock> existingProductOpt = productStockRepository.findById(productId);

        if (existingProductOpt.isPresent()) {
            ProductStock existingProduct = existingProductOpt.get();
            Double oldQty = existingProduct.getProductQty();
            Double newQty = updatedProductStock.getProductQty();
            Double qtyDifference = newQty - oldQty;

            String oldProductCategory = existingProduct.getCategory();
            String oldProductName = existingProduct.getProductName();
            String newProductName = updatedProductStock.getProductName();
            String newProductCategory = updatedProductStock.getCategory();
            Double oldPprice = existingProduct.getPurchasePrice();
            Double newPprice = updatedProductStock.getPurchasePrice();

            // Handle name change before updating the product name
            if (!oldProductCategory.equals(newProductCategory) || !oldProductName.equals(newProductName)) {
                productStockRepository.reduceRemainingQty(oldProductName, productId, oldQty);
                productStockRepository.increaseRemainingQty(newProductName, productId, newQty);
                existingProduct.setCategory(updatedProductStock.getCategory());
                existingProduct.setProductName(newProductName);
            }

            // Handle price update and cost price recalculation
            if (Double.compare(oldPprice, newPprice) != 0) {
                Double newTotalQty = existingProduct.getRemainingQty() + updatedProductStock.getProductQty();
                Double totalValue = (existingProduct.getRemainingQty() * existingProduct.getCostPrice()) +
                        (updatedProductStock.getProductQty() * updatedProductStock.getPurchasePrice());
                Double newCostPrice = totalValue / newTotalQty;
                existingProduct.setCostPrice(newCostPrice);

            }

            // Handle qty difference
            if (qtyDifference > 0) {
                productStockRepository.increaseRemainingQty(oldProductName, productId, qtyDifference);
            } else if (qtyDifference < 0) {
                productStockRepository.reduceRemainingQty(oldProductName, productId, Math.abs(qtyDifference));
            }

            // Now update remaining fields
            existingProduct.setDate(updatedProductStock.getDate());
            existingProduct.setSupplier(updatedProductStock.getSupplier());
            existingProduct.setWarehouse(updatedProductStock.getWarehouse());
            existingProduct.setProductQty(newQty);
            existingProduct.setPurchasePrice(newPprice);
            existingProduct.setRemainingQty(existingProduct.getRemainingQty() + qtyDifference);

            return productStockRepository.save(existingProduct);
        } else {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
    }

    @Transactional
    public ProductStock deleteProductEntry(Long productId) {
        Optional<ProductStock> existingProductOpt = productStockRepository.findById(productId);
        if (existingProductOpt.isPresent()) {
            ProductStock existingProduct = existingProductOpt.get();
            String oldProductName = existingProduct.getProductName();
            Double qtyDifference = existingProduct.getProductQty();
            productStockRepository.reduceRemainingQty(oldProductName, productId, qtyDifference);
            productStockRepository.deleteById(productId);
            return existingProduct;
        }
        throw new RuntimeException("Product with ID " + productId + " not found.");
    }

    public List<DailyWarehouseStockDTO> getWarehouseDailyReport(String username, LocalDate date) {
        List<Object[]> rows = productStockRepository.getWarehouseDailyStock(username, date);
        List<DailyWarehouseStockDTO> list = new ArrayList<>();
        for (Object[] r : rows) {
            String warehouse = (String) r[0];
            String category = (String) r[1];
            String productName = (String) r[2];
            Double previousQty = ((Number) r[3]).doubleValue();
            Double todayEntryQty = ((Number) r[4]).doubleValue();
            Double todaySaleQty = ((Number) r[5]).doubleValue();
            Double todayTransferInQty = ((Number) r[6]).doubleValue();
            Double todayTransferOutQty = ((Number) r[7]).doubleValue();
            Double costPrice = ((Number) r[8]).doubleValue();
            Double presentQty = previousQty + todayEntryQty - todaySaleQty
                    + todayTransferInQty - todayTransferOutQty;
            Double totalValue = presentQty * costPrice;

            list.add(new DailyWarehouseStockDTO(
                    warehouse,
                    category,
                    productName,
                    previousQty,
                    todayEntryQty,
                    todaySaleQty,
                    todayTransferInQty,
                    todayTransferOutQty,
                    presentQty,
                    costPrice,
                    totalValue));
        }
        return list;
    }

    // public List<ProductStock> getCurrentMonthData(String username) {
    // LocalDate startDate = LocalDate.now().withDayOfMonth(1);
    // LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
    // return productStockRepository.getCurrentMonthData(username, startDate,
    // endDate);
    // }

    // public List<ProductStock> getDatewiseStockLeger(String username, LocalDate
    // startDate, LocalDate endDate) {
    // return productStockRepository.getCurrentMonthData(username, startDate,
    // endDate);
    // }

    public List<StockLedgerDTO> getStockLedger(String username, LocalDate startDate, LocalDate endDate) {

        // Opening Stock
        List<Object[]> openingData = productStockRepository.getOpeningStock(username, startDate);

        // Current Transactions
        List<ProductStock> transactions = productStockRepository.getCurrentMonthData(
                username,
                startDate,
                endDate);

        // Store opening qty
        Map<String, Double> balanceMap = new HashMap<>();

        for (Object[] row : openingData) {

            String warehouse = (String) row[0];
            String category = (String) row[1];
            String product = (String) row[2];

            Double qty = ((Number) row[3]).doubleValue();

            String key = warehouse + "|" + category + "|" + product;

            balanceMap.put(key, qty);
        }

        List<StockLedgerDTO> result = new ArrayList<>();

        for (ProductStock ps : transactions) {

            String key = ps.getWarehouse()
                    + "|"
                    + ps.getCategory()
                    + "|"
                    + ps.getProductName();

            double opening = balanceMap.getOrDefault(key, 0.0);

            double stored = 0;
            double sold = 0;
            double transferIn = 0;
            double transferOut = 0;

            if ("stored".equalsIgnoreCase(ps.getStatus())) {

                stored = ps.getProductQty();

            } else if ("sold".equalsIgnoreCase(ps.getStatus())) {

                sold = ps.getProductQty();
            } else if ("transfer-in".equalsIgnoreCase(ps.getStatus())) {

                transferIn = ps.getProductQty();
            } else if ("transfer-out".equalsIgnoreCase(ps.getStatus())) {

                transferOut = ps.getProductQty();
            }

            double closing = opening + stored - sold + transferIn - transferOut;

            StockLedgerDTO dto = new StockLedgerDTO();

            dto.setDate(ps.getDate());

            dto.setWarehouse(ps.getWarehouse());

            dto.setCategory(ps.getCategory());

            dto.setProductName(ps.getProductName());

            dto.setOpeningQty(opening);

            dto.setStoredQty(stored);

            dto.setSoldQty(sold);

            dto.setTransferInQty(transferIn);

            dto.setTransferOutQty(transferOut);

            dto.setClosingQty(closing);

            dto.setCostPrice(ps.getCostPrice());

            result.add(dto);

            // Save latest balance
            balanceMap.put(key, closing);
        }

        return result;
    }
}
