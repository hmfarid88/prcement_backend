package com.example.bake_boss_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bake_boss_backend.dto.ProductRetailerDTO;
import com.example.bake_boss_backend.dto.RetailerDetailsDTO;
import com.example.bake_boss_backend.dto.SupplierDetailsDTO;
import com.example.bake_boss_backend.dto.TransportDetailsDTO;
import com.example.bake_boss_backend.entity.ProductStock;

public interface ProductStockrepository extends JpaRepository<ProductStock, Long> {

   Optional<ProductStock> findTopByProductNameAndUsernameOrderByProductIdDesc(String productName, String username);

   @Query("SELECT ps FROM ProductStock ps WHERE ps.username=:username AND ps.productId IN " +
         "(SELECT MAX(ps2.productId) FROM ProductStock ps2 GROUP BY ps2.productName)")
   List<ProductStock> findLatestProductStockForEachProductName(@Param("username") String username);

   @Query("""
    SELECT ps 
    FROM ProductStock ps
    WHERE ps.username = :username 
      AND ps.date = :date
      AND ps.productId IN (
          SELECT MAX(ps2.productId)
          FROM ProductStock ps2
          WHERE ps2.username = :username
            AND ps2.date = :date
          GROUP BY ps2.productName
      )
""")
List<ProductStock> findLatestProductStockForEachProductNameByDate(
        @Param("username") String username,
        @Param("date") LocalDate date
);


   Optional<ProductStock> findByProductId(Long productId);

   List<ProductStock> findByUsernameAndInvoiceNo(String username, String invoiceNo);

//    @Query("SELECT ps FROM ProductStock ps WHERE ps.status='sold' AND YEAR(ps.date) = :year AND MONTH(ps.date) = :month AND ps.username=:username ORDER BY ps.date")
//    List<ProductStock> findProductByStatus(@Param("year") int year, @Param("month") int month, @Param("username") String username);

@Query("""
    SELECT new com.example.bake_boss_backend.dto.ProductRetailerDTO(
        ps.date,
        r.category,
        r.salesPerson,
        ps.customer,
        ps.note,
        ps.productName,
        ps.invoiceNo,
        ps.transport,
        ps.truckNo,
        ps.rent,
        ps.dpRate,
        ps.productQty,
        ps.productId
    )
    FROM ProductStock ps
    JOIN RetailerInfo r ON ps.customer = r.retailerName
    WHERE ps.status = 'sold'
      AND YEAR(ps.date) = :year
      AND MONTH(ps.date) = :month
      AND ps.username = :username
    ORDER BY ps.date
""")
List<ProductRetailerDTO> findProductWithRetailerDetails(
    @Param("year") int year,
    @Param("month") int month,
    @Param("username") String username
);

@Query(value = """
SELECT 
    ps.warehouse,
    ps.product_name,
    COALESCE((
        SELECT SUM(p1.remaining_qty)
        FROM product_stock p1
        WHERE p1.username = :username
          AND p1.warehouse = ps.warehouse
          AND p1.product_name = ps.product_name
          AND DATE(p1.date) < :date
    ), 0) AS previousQty,

    COALESCE((
        SELECT SUM(p2.product_qty)
        FROM product_stock p2
        WHERE p2.username = :username
          AND p2.warehouse = ps.warehouse
          AND p2.product_name = ps.product_name
          AND DATE(p2.date) = :date
          AND p2.status = 'stored'
    ), 0) AS todayEntryQty,

    COALESCE((
        SELECT SUM(p3.product_qty)
        FROM product_stock p3
        WHERE p3.username = :username
          AND p3.warehouse = ps.warehouse
          AND p3.product_name = ps.product_name
          AND DATE(p3.date) = :date
          AND p3.status = 'sold'
    ), 0) AS todaySaleQty,

    COALESCE((
        SELECT p4.cost_price
        FROM product_stock p4
        WHERE p4.username = :username
          AND p4.warehouse = ps.warehouse
          AND p4.product_name = ps.product_name
        ORDER BY p4.product_id DESC
        LIMIT 1
    ), 0) AS costPrice

FROM product_stock ps
WHERE ps.username = :username
GROUP BY ps.warehouse, ps.product_name
""", nativeQuery = true)
List<Object[]> getWarehouseDailyStock(@Param("username") String username, @Param("date") LocalDate date
);


// @Query("""
// SELECT 
//     ps.warehouse,
//     ps.productName,
//     COALESCE((
//         SELECT SUM(p1.remainingQty)
//         FROM ProductStock p1
//         WHERE p1.username = :username
//           AND p1.warehouse = ps.warehouse
//           AND p1.productName = ps.productName
//           AND p1.date < :date
//     ), 0) AS previousQty,
//     COALESCE((
//         SELECT SUM(p2.productQty)
//         FROM ProductStock p2
//         WHERE p2.username = :username
//           AND p2.warehouse = ps.warehouse
//           AND p2.productName = ps.productName
//           AND p2.date = :date
//           AND p2.status = 'stored'
//     ), 0) AS todayEntryQty,
//     COALESCE((
//         SELECT SUM(p3.productQty)
//         FROM ProductStock p3
//         WHERE p3.username = :username
//           AND p3.warehouse = ps.warehouse
//           AND p3.productName = ps.productName
//           AND p3.date = :date
//           AND p3.status = 'sold'
//     ), 0) AS todaySaleQty,


// COALESCE((
//     SELECT p4.costPrice
//     FROM ProductStock p4
//     WHERE p4.username = :username
//       AND p4.warehouse = ps.warehouse
//       AND p4.productName = ps.productName
//       AND p4.productId = (
//           SELECT MAX(p5.productId)
//           FROM ProductStock p5
//           WHERE p5.username = :username
//             AND p5.warehouse = ps.warehouse
//             AND p5.productName = ps.productName
//       )
// ), 0) AS costPrice


// FROM ProductStock ps
// WHERE ps.username = :username
// GROUP BY ps.warehouse, ps.productName
// """)
// List<Object[]> getWarehouseDailyStock(
//         @Param("username") String username,
//         @Param("date") LocalDate date
// );

@Query("""
    SELECT new com.example.bake_boss_backend.dto.ProductRetailerDTO(
        ps.date,
        r.category,
        r.salesPerson,
        ps.customer,
        ps.note,
        ps.productName,
        ps.invoiceNo,
        ps.transport,
        ps.truckNo,
        ps.rent,
        ps.dpRate,
        ps.productQty,
        ps.productId
    )
    FROM ProductStock ps
    JOIN RetailerInfo r ON ps.customer = r.retailerName
    WHERE ps.status = 'sold'AND ps.date BETWEEN :startDate AND :endDate AND ps.username = :username ORDER BY ps.date
     
""")
List<ProductRetailerDTO> findDatewiseSoldProductByUsername(
    LocalDate startDate, LocalDate endDate, String username
    
);
//    @Query("SELECT ps FROM ProductStock ps WHERE ps.status='sold' AND  ps.username=:username AND ps.date BETWEEN :startDate AND :endDate ORDER BY ps.date")
//    List<ProductStock> findDatewiseSoldProductByUsername(String username, LocalDate startDate, LocalDate endDate);

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
   Double getSoldValueForRetailers(@Param("retailerNames") List<String> retailerNames, @Param("year") int year, @Param("month") int month);

   @Query("SELECT SUM(ps.productQty) FROM ProductStock ps WHERE ps.status = 'sold' AND ps.date = CURRENT_DATE")
   Double findTotalSoldProductQtyToday();

   @Query("SELECT ps.remainingQty FROM ProductStock ps " +
         "WHERE ps.username = :username AND ps.productName = :productName " +
         "ORDER BY ps.productId DESC LIMIT 1")
   Double findLastRemainingQtyByUsernameAndProductName(@Param("username") String username, @Param("productName") String productName);

   @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0, 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.customer = :retailerName AND (YEAR(ps.date) < :year OR (YEAR(ps.date) = :year AND MONTH(ps.date) < :month))")
   List<RetailerDetailsDTO> findProductDetailsByUsernameAndRetailerNameBeforemonth(String username, String retailerName,  int year,  int month);
   
   @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0, 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.customer = :retailerName AND ps.date < :startDate")
   List<RetailerDetailsDTO> findProductDetailsByUsernameAndRetailerNameBeforeDate(String username, String retailerName,  LocalDate startDate);
   
   @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0, 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.customer = :retailerName AND YEAR(ps.date) = :year AND MONTH(ps.date) = :month")
   List<RetailerDetailsDTO> findProductDetailsByUsernameAndRetailerNameCurrentmonth(String username, String retailerName,  int year,  int month);

   @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0, 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.customer = :retailerName AND ps.date BETWEEN :startDate AND :endDate")
   List<RetailerDetailsDTO> findProductDetailsByUsernameAndRetailerNameDatetodate(String username, String retailerName,  LocalDate startDate,  LocalDate endDate);

//    @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0, 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.customer = :retailerName AND ps.date BETWEEN :startDate AND :endDate")
//    List<RetailerDetailsDTO> findProductDetailsByUsernameAndRetailerName(String username, String retailerName,
//          LocalDate startDate, LocalDate endDate);

   @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0, 0.0) FROM ProductStock ps JOIN RetailerInfo ri ON ps.customer = ri.retailerName WHERE ri.salesPerson = :salesPerson AND ps.customer = :retailerName AND ps.status='sold' AND ps.date BETWEEN :startDate AND :endDate")
   List<RetailerDetailsDTO> findProductDetailsBySalesPersonAndRetailerName(String salesPerson, String retailerName,
         LocalDate startDate, LocalDate endDate);

   @Query("SELECT new com.example.bake_boss_backend.dto.SupplierDetailsDTO(ps.date, ps.productName, ps.productQty, ps.productQty*ps.purchasePrice, 0.0, 0.0, 'No') FROM ProductStock ps WHERE ps.username = :username AND ps.status='stored' AND ps.supplier = :supplierName  AND FUNCTION('YEAR', ps.date) = FUNCTION('YEAR', CURRENT_DATE) AND FUNCTION('MONTH', ps.date) = FUNCTION('MONTH', CURRENT_DATE)")
   List<SupplierDetailsDTO> findProductDetailsByUsernameAndSupplierName(String username, String supplierName);

   @Query("SELECT new com.example.bake_boss_backend.dto.SupplierDetailsDTO(ps.date, ps.productName, ps.productQty, ps.productQty*ps.purchasePrice, 0.0, 0.0, 'No') FROM ProductStock ps WHERE ps.username = :username AND ps.status='stored' AND ps.supplier = :supplierName  AND ps.date BETWEEN :startDate AND :endDate")
   List<SupplierDetailsDTO> findDatewiseProductDetailsByUsernameAndSupplierName(String username, String supplierName, LocalDate startDate, LocalDate endDate);

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
