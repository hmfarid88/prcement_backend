package com.example.bake_boss_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bake_boss_backend.dto.MonthlySaleDTO;
import com.example.bake_boss_backend.dto.MonthlySrSaleDTO;
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
                        @Param("date") LocalDate date);

        Optional<ProductStock> findByProductId(Long productId);

        List<ProductStock> findByUsernameAndInvoiceNo(String username, String invoiceNo);

        // @Query("SELECT ps FROM ProductStock ps WHERE ps.status='sold' AND
        // YEAR(ps.date) = :year AND MONTH(ps.date) = :month AND ps.username=:username
        // ORDER BY ps.date")
        // List<ProductStock> findProductByStatus(@Param("year") int year,
        // @Param("month") int month, @Param("username") String username);

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
                                ps.costPrice,
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
                        @Param("username") String username);

        @Query(value = """
                        SELECT
                            ps.warehouse,
                            ps.product_name,

                            COALESCE((
                                SELECT ps2.remaining_qty
                                FROM product_stock ps2
                                WHERE ps2.username = :username
                                  AND ps2.warehouse = ps.warehouse
                                  AND ps2.product_name = ps.product_name
                                  AND ps2.date < :date
                                ORDER BY ps2.date DESC
                                LIMIT 1
                            ), 0) AS previousQty,

                            COALESCE(SUM(
                                CASE
                                    WHEN ps.date = :date
                                     AND ps.status = 'stored'
                                    THEN ps.product_qty
                                END
                            ), 0) AS todayEntryQty,

                            COALESCE(SUM(
                                CASE
                                    WHEN ps.date = :date
                                     AND ps.status = 'sold'
                                    THEN ps.product_qty
                                END
                            ), 0) AS todaySaleQty,

                            COALESCE((
                                SELECT ps3.cost_price
                                FROM product_stock ps3
                                WHERE ps3.username = :username
                                  AND ps3.warehouse = ps.warehouse
                                  AND ps3.product_name = ps.product_name
                                ORDER BY ps3.date DESC
                                LIMIT 1
                            ), 0) AS costPrice

                        FROM product_stock ps
                        WHERE ps.username = :username
                        GROUP BY ps.warehouse, ps.product_name ORDER BY ps.product_name;
                        """, nativeQuery = true)
        List<Object[]> getWarehouseDailyStock(
                        @Param("username") String username,
                        @Param("date") LocalDate date);

        // @Query(value = """
        // SELECT
        // ps.warehouse,
        // ps.product_name,

        // COALESCE(
        // today.remaining_qty,
        // prev.remaining_qty,
        // 0
        // ) AS previousQty,

        // COALESCE(SUM(
        // CASE WHEN ps.status = 'stored' THEN ps.product_qty END
        // ), 0) AS todayEntryQty,

        // COALESCE(SUM(
        // CASE WHEN ps.status = 'sold' THEN ps.product_qty END
        // ), 0) AS todaySaleQty,

        // MAX(ps.cost_price) AS costPrice

        // FROM product_stock ps

        // LEFT JOIN product_stock today
        // ON today.product_id = (
        // SELECT t.product_id
        // FROM product_stock t
        // WHERE t.username = ps.username
        // AND t.warehouse = ps.warehouse
        // AND t.product_name = ps.product_name
        // AND t.date = :date
        // ORDER BY t.product_id DESC
        // LIMIT 1
        // )

        // LEFT JOIN product_stock prev
        // ON prev.product_id = (
        // SELECT p.product_id
        // FROM product_stock p
        // WHERE p.username = ps.username
        // AND p.warehouse = ps.warehouse
        // AND p.product_name = ps.product_name
        // AND p.date < :date
        // ORDER BY p.date DESC, p.product_id DESC
        // LIMIT 1
        // )

        // WHERE ps.username = :username
        // AND ps.date = :date

        // GROUP BY
        // ps.warehouse,
        // ps.product_name,
        // today.remaining_qty,
        // prev.remaining_qty
        // """, nativeQuery = true)
        // List<Object[]> getWarehouseDailyStock(
        // @Param("username") String username,
        // @Param("date") LocalDate date
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
                                ps.costPrice,
                                ps.dpRate,
                                ps.productQty,
                                ps.productId
                            )
                            FROM ProductStock ps
                            JOIN RetailerInfo r ON ps.customer = r.retailerName
                            WHERE ps.status = 'sold'AND ps.date BETWEEN :startDate AND :endDate AND ps.username = :username ORDER BY ps.date, ps.customer

                        """)
        List<ProductRetailerDTO> findDatewiseSoldProductByUsername(
                        LocalDate startDate, LocalDate endDate, String username

        );
        // @Query("SELECT ps FROM ProductStock ps WHERE ps.status='sold' AND
        // ps.username=:username AND ps.date BETWEEN :startDate AND :endDate ORDER BY
        // ps.date")
        // List<ProductStock> findDatewiseSoldProductByUsername(String username,
        // LocalDate startDate, LocalDate endDate);

        @Query("SELECT ps FROM ProductStock ps JOIN RetailerInfo ri ON ps.customer = ri.retailerName WHERE ri.salesPerson = :username AND ps.status='sold' AND YEAR(ps.date) = :year AND MONTH(ps.date) = :month ORDER BY ps.date")
        List<ProductStock> findProductBySalesPerson(@Param("year") int year, @Param("month") int month,
                        @Param("username") String username);

        @Query("SELECT ps FROM ProductStock ps JOIN RetailerInfo ri ON ps.customer = ri.retailerName WHERE ri.salesPerson = :username AND ps.status='sold' AND ps.date BETWEEN :startDate AND :endDate ORDER BY ps.date")
        List<ProductStock> findDatewiseProductBySalesPerson(String username, LocalDate startDate, LocalDate endDate);

        @Query("SELECT ps FROM ProductStock ps WHERE ps.status='stored' AND YEAR(ps.date) = :year AND MONTH(ps.date) = :month AND ps.username=:username ORDER BY ps.date, ps.productName")
        List<ProductStock> findProductByUsername(@Param("year") int year, @Param("month") int month,
                        @Param("username") String username);

        @Query("SELECT ps FROM ProductStock ps WHERE ps.status='stored' AND  ps.username=:username AND ps.date BETWEEN :startDate AND :endDate ORDER BY ps.date, ps.productName")
        List<ProductStock> findDatewiseProductByUsername(String username, LocalDate startDate, LocalDate endDate);

        List<ProductStock> findByUsernameAndProductName(String username, String oldItemName);

        @Query("SELECT ps.supplier, SUM(ps.purchasePrice * ps.productQty) " +
                        "FROM ProductStock ps WHERE ps.username = :username AND ps.status='stored' AND ps.supplier!='opening' GROUP BY ps.supplier")
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
        Double getTotalProductQtyForRetailers(@Param("retailerNames") List<String> retailerNames,
                        @Param("year") int year,
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

        @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0, 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.customer = :retailerName AND (YEAR(ps.date) < :year OR (YEAR(ps.date) = :year AND MONTH(ps.date) < :month))")
        List<RetailerDetailsDTO> findProductDetailsByUsernameAndRetailerNameBeforemonth(String username,
                        String retailerName, int year, int month);

        @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0, 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.customer = :retailerName AND ps.date < :startDate")
        List<RetailerDetailsDTO> findProductDetailsByUsernameAndRetailerNameBeforeDate(String username,
                        String retailerName,
                        LocalDate startDate);

        @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0, 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.customer = :retailerName AND YEAR(ps.date) = :year AND MONTH(ps.date) = :month")
        List<RetailerDetailsDTO> findProductDetailsByUsernameAndRetailerNameCurrentmonth(String username,
                        String retailerName, int year, int month);

        @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0, 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.customer = :retailerName AND ps.date BETWEEN :startDate AND :endDate")
        List<RetailerDetailsDTO> findProductDetailsByUsernameAndRetailerNameDatetodate(String username,
                        String retailerName,
                        LocalDate startDate, LocalDate endDate);

        // @Query("SELECT new
        // com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note,
        // ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0,
        // 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold'
        // AND ps.customer = :retailerName AND ps.date BETWEEN :startDate AND :endDate")
        // List<RetailerDetailsDTO> findProductDetailsByUsernameAndRetailerName(String
        // username, String retailerName,
        // LocalDate startDate, LocalDate endDate);

        @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(ps.date, ps.note, ps.productName, ps.productQty, ps.dpRate, ps.productQty*ps.dpRate, 0.0, 0.0, 0.0) FROM ProductStock ps JOIN RetailerInfo ri ON ps.customer = ri.retailerName WHERE ri.salesPerson = :salesPerson AND ps.customer = :retailerName AND ps.status='sold' AND ps.date BETWEEN :startDate AND :endDate")
        List<RetailerDetailsDTO> findProductDetailsBySalesPersonAndRetailerName(String salesPerson, String retailerName,
                        LocalDate startDate, LocalDate endDate);

        @Query("""
                        SELECT COALESCE(SUM(ps.productQty * ps.purchasePrice), 0)
                        FROM ProductStock ps
                        WHERE ps.username = :username
                        AND ps.supplier = :supplierName
                        AND ps.status = 'stored'
                        AND ps.date < :startOfMonth
                        """)
        Double getTotalProductValueBeforeMonth(
                        String username,
                        String supplierName,
                        LocalDate startOfMonth);

        @Query("SELECT new com.example.bake_boss_backend.dto.SupplierDetailsDTO(ps.date, ps.productName, ps.productQty, ps.productQty*ps.purchasePrice, 0.0, 0.0, 'No') FROM ProductStock ps WHERE ps.username = :username AND ps.status='stored' AND ps.supplier = :supplierName  AND FUNCTION('YEAR', ps.date) = FUNCTION('YEAR', CURRENT_DATE) AND FUNCTION('MONTH', ps.date) = FUNCTION('MONTH', CURRENT_DATE)")
        List<SupplierDetailsDTO> findProductDetailsByUsernameAndSupplierName(String username, String supplierName);

        @Query("SELECT new com.example.bake_boss_backend.dto.SupplierDetailsDTO(ps.date, ps.productName, ps.productQty, ps.productQty*ps.purchasePrice, 0.0, 0.0, 'No') FROM ProductStock ps WHERE ps.username = :username AND ps.status='stored' AND ps.supplier = :supplierName  AND ps.date BETWEEN :startDate AND :endDate")
        List<SupplierDetailsDTO> findDatewiseProductDetailsByUsernameAndSupplierName(String username,
                        String supplierName,
                        LocalDate startDate, LocalDate endDate);

        @Query("SELECT new com.example.bake_boss_backend.dto.TransportDetailsDTO(ps.date, ps.truckNo, SUM(ps.productQty), SUM(ps.rent), 0.0) FROM ProductStock ps WHERE ps.username = :username AND ps.status='sold' AND ps.transport = :transport  GROUP BY ps.date, ps.truckNo")
        List<TransportDetailsDTO> findRentDetailsByUsernameAndSupplierName(String username, String transport);

        @Modifying
        @Query("UPDATE ProductStock s SET s.remainingQty = s.remainingQty - :qtyDifference " +
                        "WHERE s.productName = :productName  AND s.productId >= :productId")
        void reduceRemainingQty(@Param("productName") String productName, @Param("productId") Long productId,
                        @Param("qtyDifference") Double qtyDifference);

        @Modifying
        @Query("UPDATE ProductStock s SET s.remainingQty = s.remainingQty + :qtyDifference " +
                        "WHERE s.productName = :productName AND s.productId >= :productId")
        void increaseRemainingQty(@Param("productName") String productName, @Param("productId") Long productId,
                        @Param("qtyDifference") Double qtyDifference);

        @Query("SELECT p.remainingQty FROM ProductStock p WHERE p.productName = :productName AND p.productId < :productId ORDER BY p.productId DESC LIMIT 1")
        Double findRemainingQtyByProductName(@Param("productName") String productName,
                        @Param("productId") Long productId);

        @Modifying
        @Query("UPDATE ProductStock p SET p.remainingQty = :newRemainingQty WHERE p.productName = :productName AND p.productId = :productId")
        void updateRemainingQtyByProductName(@Param("productName") String productName,
                        @Param("productId") Long productId,
                        @Param("newRemainingQty") Double newRemainingQty);

        @Query("""
                            SELECT COALESCE(SUM(
                                (ps.dpRate * ps.productQty) - (ps.costPrice * ps.productQty)
                            ), 0)
                            FROM ProductStock ps
                            WHERE ps.username = :username
                              AND ps.status = 'sold'
                               AND YEAR(ps.date) = YEAR(CURRENT_DATE)
                               AND MONTH(ps.date) = MONTH(CURRENT_DATE)
                        """)
        Double getMonthlySalesProfit(String username);

        @Query("""
                            SELECT COALESCE(SUM(
                                (ps.dpRate * ps.productQty) - (ps.costPrice * ps.productQty)
                            ), 0)
                            FROM ProductStock ps
                            WHERE ps.username = :username
                              AND ps.status = 'sold'
                              AND ps.date BETWEEN :from AND :to
                        """)
        Double getSalesProfit(String username, LocalDate from, LocalDate to);

        @Query("""
                            SELECT COALESCE(SUM(ps.rent), 0)
                            FROM ProductStock ps
                            WHERE ps.username = :username
                              AND ps.status = 'sold'
                              AND YEAR(ps.date) = YEAR(CURRENT_DATE)
                            AND MONTH(ps.date) = MONTH(CURRENT_DATE)
                        """)
        Double getMonthlyTotalRent(String username);

        @Query("""
                            SELECT COALESCE(SUM(ps.rent), 0)
                            FROM ProductStock ps
                            WHERE ps.username = :username
                              AND ps.status = 'sold'
                              AND ps.date BETWEEN :from AND :to
                        """)
        Double getTotalRent(String username, LocalDate from, LocalDate to);

        @Query("""
                        SELECT new com.example.bake_boss_backend.dto.MonthlySaleDTO(
                            MONTH(ps.date),
                            SUM(ps.dpRate * ps.productQty)
                        )
                        FROM ProductStock ps
                        WHERE ps.status = 'sold'
                          AND ps.username = :username
                          AND ps.date BETWEEN :startDate AND :endDate
                        GROUP BY MONTH(ps.date)
                        ORDER BY MONTH(ps.date)
                        """)
        List<MonthlySaleDTO> findMonthlySaleSummary(
                        @Param("username") String username,
                        @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

        @Query("""
                            SELECT new com.example.bake_boss_backend.dto.MonthlySrSaleDTO(
                                ri.salesPerson,
                                MONTH(ps.date),
                                SUM(ps.productQty),
                                SUM(ps.dpRate * ps.productQty)
                            )
                            FROM ProductStock ps
                            JOIN RetailerInfo ri
                                 ON ps.customer = ri.retailerName
                            WHERE ps.status = 'sold'
                              AND ps.username = :username
                              AND ps.date BETWEEN :startDate AND :endDate
                            GROUP BY ri.salesPerson, MONTH(ps.date)
                            ORDER BY MONTH(ps.date)
                        """)
        List<MonthlySrSaleDTO> findMonthlySrSaleSummary(
                        @Param("username") String username,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

@Query("SELECT COALESCE(SUM(p.costPrice*p.productQty), 0) FROM ProductStock p WHERE p.supplier = 'opening'")
    Double findTotalOpeningStock();

@Query("""
    SELECT COALESCE(SUM(p.productQty * p.dpRate), 0)
    FROM ProductStock p
    WHERE p.status = 'sold'
  """)
Double getTotalSoldValueByRetailer();

@Query("""
    SELECT COALESCE(SUM(p.productQty * p.purchasePrice), 0)
    FROM ProductStock p
    WHERE p.status = 'stored' AND p.supplier!='opening'
  """)
Double getTotalStockValue();
}
