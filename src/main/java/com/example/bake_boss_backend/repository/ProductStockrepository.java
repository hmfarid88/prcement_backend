package com.example.bake_boss_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bake_boss_backend.dto.RetailerDetailsDTO;
import com.example.bake_boss_backend.dto.SupplierDetailsDTO;
import com.example.bake_boss_backend.dto.TransportDetailsDTO;
import com.example.bake_boss_backend.entity.ProductStock;

public interface ProductStockrepository extends JpaRepository<ProductStock, Long> {

   Optional<ProductStock> findTopByProductNameAndUsernameOrderByProductIdDesc(String productName, String username);

   @Query("SELECT ps FROM ProductStock ps WHERE ps.username=:username AND ps.productId IN " +
         "(SELECT MAX(ps2.productId) FROM ProductStock ps2 GROUP BY ps2.productName)")
   List<ProductStock> findLatestProductStockForEachProductName(@Param("username") String username);

   Optional<ProductStock> findByProductId(Long productId);

   List<ProductStock> findByUsernameAndInvoiceNo(String username, String invoiceNo);

   @Query("SELECT ps FROM ProductStock ps WHERE ps.status='sold' AND YEAR(ps.date) = :year AND MONTH(ps.date) = :month AND ps.username=:username ORDER BY ps.date")
   List<ProductStock> findProductByStatus(@Param("year") int year, @Param("month") int month, @Param("username") String username);

   @Query("SELECT ps FROM ProductStock ps WHERE ps.status='sold' AND  ps.username=:username AND ps.date BETWEEN :startDate AND :endDate ORDER BY ps.date")
   List<ProductStock> findDatewiseSoldProductByUsername(String username, LocalDate startDate, LocalDate endDate);

   @Query("SELECT ps FROM ProductStock ps JOIN RetailerInfo ri ON ps.customer = ri.retailerName WHERE ri.salesPerson = :username AND ps.status='sold' AND YEAR(ps.date) = :year AND MONTH(ps.date) = :month ORDER BY ps.date")
   List<ProductStock> findProductBySalesPerson(@Param("year") int year, @Param("month") int month, @Param("username") String username);

   @Query("SELECT ps FROM ProductStock ps JOIN RetailerInfo ri ON ps.customer = ri.retailerName WHERE ri.salesPerson = :username AND ps.status='sold' AND ps.date BETWEEN :startDate AND :endDate ORDER BY ps.date")
   List<ProductStock> findDatewiseProductBySalesPerson(String username, LocalDate startDate, LocalDate endDate);

   @Query("SELECT ps FROM ProductStock ps WHERE  YEAR(ps.date) = :year AND MONTH(ps.date) = :month AND ps.username=:username ORDER BY ps.date")
   List<ProductStock> findProductByUsername(@Param("year") int year, @Param("month") int month, @Param("username") String username);

   @Query("SELECT ps FROM ProductStock ps WHERE  ps.username=:username AND ps.date BETWEEN :startDate AND :endDate ORDER BY ps.date")
   List<ProductStock> findDatewiseProductByUsername(String username, LocalDate startDate, LocalDate endDate);

   List<ProductStock> findByUsernameAndProductName(String username, String oldItemName);

   @Query("SELECT ps.supplier, SUM(ps.purchasePrice * ps.productQty) " +
         "FROM ProductStock ps WHERE ps.username = :username AND ps.status='stored' GROUP BY ps.supplier")
   List<Object[]> findTotalProductCostGroupedBySupplierAndUsername(String username);

   @Query("SELECT ps.transport, SUM(ps.rent) " +
         "FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' GROUP BY ps.transport")
   List<Object[]> findTotalRentGroupedByTransportAndUsername(String username);

   @Query("SELECT SUM(ps.dpRate * ps.productQty) FROM ProductStock ps " +
         "JOIN RetailerInfo ri ON ps.customer = ri.retailerName " +
         "WHERE ps.status = 'sold' AND ri.salesPerson = :employeeName " +
         "AND EXTRACT(YEAR FROM ps.date) = :year " +
         "AND EXTRACT(MONTH FROM ps.date) = :month")
   Double getSoldValueForEmployee(@Param("employeeName") String employeeName, @Param("year") int year,
         @Param("month") int month);

   @Query("SELECT SUM(ps.productQty) FROM ProductStock ps WHERE ps.customer IN :retailerNames AND YEAR(ps.date) = :year AND MONTH(ps.date) = :month AND ps.status = 'sold'")
   Double getTotalProductQtyForRetailers(@Param("retailerNames") List<String> retailerNames, @Param("year") int year,
         @Param("month") int month);

   @Query("SELECT SUM(ps.dpRate * ps.productQty) FROM ProductStock ps WHERE ps.customer IN :retailerNames AND YEAR(ps.date) = :year AND MONTH(ps.date) = :month AND ps.status = 'sold'")
   Double getSoldValueForRetailers(@Param("retailerNames") List<String> retailerNames, @Param("year") int year,
         @Param("month") int month);

   @Query("SELECT SUM(ps.productQty) FROM ProductStock ps WHERE ps.status = 'sold' AND ps.date = CURRENT_DATE")
   Double findTotalSoldProductQtyToday();

   @Query("SELECT ps.remainingQty FROM ProductStock ps " +
         "WHERE ps.username = :username AND ps.productName = :productName " +
         "ORDER BY ps.productId DESC LIMIT 1")
   Double findLastRemainingQtyByUsernameAndProductName(@Param("username") String username,
         @Param("productName") String productName);

   @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.customer = :retailerName AND ps.date BETWEEN :startDate AND :endDate")
   List<RetailerDetailsDTO> findProductDetailsByUsernameAndRetailerName(String username, String retailerName,
         LocalDate startDate, LocalDate endDate);

   @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0) FROM ProductStock ps JOIN RetailerInfo ri ON ps.customer = ri.retailerName WHERE ri.salesPerson = :salesPerson AND ps.customer = :retailerName AND ps.status='sold' AND ps.date BETWEEN :startDate AND :endDate")
   List<RetailerDetailsDTO> findProductDetailsBySalesPersonAndRetailerName(String salesPerson, String retailerName,
         LocalDate startDate, LocalDate endDate);

   @Query("SELECT new com.example.bake_boss_backend.dto.SupplierDetailsDTO(ps.date, ps.productName, SUM(ps.productQty), SUM(ps.productQty*ps.purchasePrice), 0.0, 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='stored' AND ps.supplier = :supplierName  GROUP BY ps.date, ps.productName")
   List<SupplierDetailsDTO> findProductDetailsByUsernameAndSupplierName(String username, String supplierName);

   @Query("SELECT new com.example.bake_boss_backend.dto.TransportDetailsDTO(ps.date, ps.truckNo, SUM(ps.productQty), SUM(ps.rent), 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.transport = :transport  GROUP BY ps.date, ps.truckNo")
   List<TransportDetailsDTO> findRentDetailsByUsernameAndSupplierName(String username, String transport);

   @Modifying
   @Query("UPDATE ProductStock s SET s.remainingQty = s.remainingQty - :qtyDifference " +
         "WHERE s.productName = :productName  AND s.productId >= :productId")
   void reduceRemainingQty(@Param("productName") String productName, @Param("productId") Long productId, @Param("qtyDifference") Double qtyDifference);

   @Modifying
   @Query("UPDATE ProductStock s SET s.remainingQty = s.remainingQty + :qtyDifference " +
         "WHERE s.productName = :productName AND s.productId >= :productId")
   void increaseRemainingQty(@Param("productName") String productName, @Param("productId") Long productId, @Param("qtyDifference") Double qtyDifference);
  
   @Query("SELECT p.remainingQty FROM ProductStock p WHERE p.productName = :productName AND p.productId < :productId ORDER BY p.productId DESC LIMIT 1")
   Double findRemainingQtyByProductName(@Param("productName") String productName, @Param("productId") Long productId);

   @Modifying
@Query("UPDATE ProductStock p SET p.remainingQty = :newRemainingQty WHERE p.productName = :productName AND p.productId = :productId")
void updateRemainingQtyByProductName(@Param("productName") String productName, @Param("productId") Long productId, @Param("newRemainingQty") Double newRemainingQty);
}
