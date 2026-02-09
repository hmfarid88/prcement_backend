package com.example.bake_boss_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bake_boss_backend.dto.CategoryBalanceDTO;
import com.example.bake_boss_backend.dto.ReceiveDto;
import com.example.bake_boss_backend.dto.RetailerBalanceDTO;
import com.example.bake_boss_backend.dto.RetailerDetailsDTO;
import com.example.bake_boss_backend.entity.RetailerPayment;

public interface RetailerPaymentRepository extends JpaRepository<RetailerPayment, Long> {
    @Query(value = "SELECT ( " +
            "  (SELECT COALESCE(SUM(amount), 0) FROM retailer_payment WHERE username = :username AND DATE(date) < :date) + "
            +
            "  (SELECT COALESCE(SUM(amount), 0) FROM office_receive WHERE username = :username AND DATE(date) < :date) "
            +
            ") - ( " +
            "  (SELECT COALESCE(SUM(amount), 0) FROM expense WHERE username = :username AND DATE(date) < :date) + "
            +
            "  (SELECT COALESCE(SUM(amount), 0) FROM office_payment WHERE username = :username AND DATE(date) < :date) + "
            +
            "  (SELECT COALESCE(SUM(amount), 0) FROM supplier_payment WHERE username = :username AND DATE(date) < :date) + "
            +

            "  (SELECT COALESCE(SUM(amount), 0) FROM transport_payment WHERE username = :username AND DATE(date) < :date) + "
            +
            "  (SELECT COALESCE(SUM(amount), 0) FROM employee_payment WHERE username = :username AND DATE(date) < :date) "
            +
            ") AS total_amount", nativeQuery = true)
    Double findNetSumAmountBeforeToday(@Param("username") String username, @Param("date") LocalDate date);

    @Query(value = "SELECT ( " +
            "  (SELECT COALESCE(SUM(amount), 0) FROM retailer_payment WHERE username = :username) + "
            +
            "  (SELECT COALESCE(SUM(amount), 0) FROM office_receive WHERE username = :username) "
            +
            ") - ( " +
            "  (SELECT COALESCE(SUM(amount), 0) FROM expense WHERE username = :username) + "
            +
            "  (SELECT COALESCE(SUM(amount), 0) FROM office_payment WHERE username = :username) + "
            +
            "  (SELECT COALESCE(SUM(amount), 0) FROM supplier_payment WHERE username = :username) + "
            +

            "  (SELECT COALESCE(SUM(amount), 0) FROM transport_payment WHERE username = :username) + "
            +
            "  (SELECT COALESCE(SUM(amount), 0) FROM employee_payment WHERE username = :username) "
            +
            ") AS total_amount", nativeQuery = true)
    Double findNetSumAmountToday(@Param("username") String username);

    @Query("SELECT new com.example.bake_boss_backend.dto.ReceiveDto(rp.date, rp.retailerName, rp.note, rp.amount) "
            +
            "FROM RetailerPayment rp " +
            "WHERE rp.username = :username AND rp.date = :date")
    List<ReceiveDto> findRetailerPaymentsForToday(@Param("username") String username, @Param("date") LocalDate date);

    @Query("SELECT o FROM RetailerPayment o WHERE YEAR(o.date) = :year AND MONTH(o.date) = :month AND o.username = :username ORDER BY o.date")
    List<RetailerPayment> findRetailerPayByMonth(@Param("year") int year, @Param("month") int month,
            @Param("username") String username);

    @Query("SELECT ps FROM RetailerPayment ps WHERE  ps.username=:username AND ps.date BETWEEN :startDate AND :endDate ORDER BY ps.date")
    List<RetailerPayment> findDatewiseRetailerPaymentByUsername(String username, LocalDate startDate,
            LocalDate endDate);

    @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(rp.date, rp.note, 'No', 0.0, 0.0, 0.0, rp.amount, 0.0, 0.0) FROM RetailerPayment rp WHERE rp.username = :username AND  rp.retailerName = :retailerName AND (YEAR(rp.date) < :year OR (YEAR(rp.date) = :year AND MONTH(rp.date) < :month))")
    List<RetailerDetailsDTO> findPaymentDetailsByUsernameAndRetailerNameBeforemonth(String username,
            String retailerName, int year, int month);

    @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(rp.date, rp.note, 'No', 0.0, 0.0, 0.0, rp.amount, 0.0, 0.0) FROM RetailerPayment rp WHERE rp.username = :username AND  rp.retailerName = :retailerName AND rp.date < :startDate")
    List<RetailerDetailsDTO> findPaymentDetailsByUsernameAndRetailerNameBeforedate(String username,
            String retailerName, LocalDate startDate);

    @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(rp.date, rp.note, 'No', 0.0, 0.0, 0.0, rp.amount, 0.0, 0.0) FROM RetailerPayment rp WHERE rp.username = :username AND  rp.retailerName = :retailerName AND YEAR(rp.date) = :year AND MONTH(rp.date) = :month")
    List<RetailerDetailsDTO> findPaymentDetailsByUsernameAndRetailerNameCurrentmonth(String username,
            String retailerName, int year, int month);

    @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(rp.date, rp.note, 'No', 0.0, 0.0, 0.0, rp.amount, 0.0, 0.0) FROM RetailerPayment rp WHERE rp.username = :username AND  rp.retailerName = :retailerName AND rp.date BETWEEN :startDate AND :endDate")
    List<RetailerDetailsDTO> findPaymentDetailsByUsernameAndRetailerNameDatetodate(String username,
            String retailerName, LocalDate startDate, LocalDate endDate);

    // @Query("SELECT new
    // com.example.bake_boss_backend.dto.RetailerDetailsDTO(rp.date, rp.note, 'No',
    // 0.0, 0.0, 0.0, rp.amount, 0.0, 0.0) FROM RetailerPayment rp WHERE rp.username
    // = :username AND rp.retailerName = :retailerName AND rp.date BETWEEN
    // :startDate AND :endDate")
    // List<RetailerDetailsDTO> findPaymentDetailsByUsernameAndRetailerName(String
    // username, String retailerName, LocalDate startDate, LocalDate endDate);

    @Query("SELECT new com.example.bake_boss_backend.dto.RetailerDetailsDTO(rp.date, rp.note, 'No', 0.0, 0.0, 0.0, rp.amount, 0.0, 0.0) FROM RetailerPayment rp JOIN RetailerInfo ri ON rp.retailerName = ri.retailerName WHERE ri.salesPerson = :salesPerson AND rp.retailerName = :retailerName AND rp.date BETWEEN :startDate AND :endDate")
    List<RetailerDetailsDTO> findPaymentDetailsBySalesPersonAndRetailerName(String salesPerson, String retailerName,
            LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                r.category, 
                COALESCE(SUM(ps.productQty), 0),
                COALESCE(SUM(ps.productQty * ps.dpRate), 0),
                (SELECT COALESCE(SUM(rp.amount), 0)
                    FROM RetailerPayment rp
                    WHERE rp.retailerName = r.retailerName
                    AND FUNCTION('YEAR', rp.date) = FUNCTION('YEAR', CURRENT_DATE)
                    AND FUNCTION('MONTH', rp.date) = FUNCTION('MONTH', CURRENT_DATE)),
                (SELECT COALESCE(SUM(rc.amount), 0)
                    FROM RetailerCommission rc
                    WHERE rc.retailerName = r.retailerName
                    AND FUNCTION('YEAR', rc.date) = FUNCTION('YEAR', CURRENT_DATE)
                    AND FUNCTION('MONTH', rc.date) = FUNCTION('MONTH', CURRENT_DATE))
            )
            FROM RetailerInfo r
            LEFT JOIN ProductStock ps
                ON ps.customer = r.retailerName
                AND FUNCTION('YEAR', ps.date) = FUNCTION('YEAR', CURRENT_DATE)
                AND FUNCTION('MONTH', ps.date) = FUNCTION('MONTH', CURRENT_DATE)
            WHERE r.status = 'Active'
            GROUP BY r.category, r.retailerName ORDER BY r.category ASC 
            """)
    List<CategoryBalanceDTO> findCategoryRawBalanceForCurrentMonth();

    @Query("""
            SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                r.category,
                COALESCE(SUM(ps.productQty), 0),
                COALESCE(SUM(ps.productQty * ps.dpRate), 0),

                (SELECT COALESCE(SUM(rp.amount), 0)
                 FROM RetailerPayment rp
                 WHERE rp.retailerName = r.retailerName
                   AND rp.date BETWEEN :startDate AND :endDate),

                (SELECT COALESCE(SUM(rc.amount), 0)
                 FROM RetailerCommission rc
                 WHERE rc.retailerName = r.retailerName
                   AND rc.date BETWEEN :startDate AND :endDate)
            )
            FROM RetailerInfo r
            LEFT JOIN ProductStock ps
              ON ps.customer = r.retailerName
             AND ps.date BETWEEN :startDate AND :endDate
            WHERE r.status = 'Active'
            GROUP BY r.category, r.retailerName
            ORDER BY r.category ASC
            """)
    List<CategoryBalanceDTO> findDatewiseCategoryBalance(LocalDate startDate, LocalDate endDate);

    @Query("""
                SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                    r.category,
                    COALESCE(SUM(ps.productQty), 0),
                    COALESCE(SUM(ps.productQty * ps.dpRate), 0),

                    (SELECT COALESCE(SUM(rp.amount), 0)
                     FROM RetailerPayment rp
                     WHERE rp.retailerName = r.retailerName
                       AND rp.date < :startDate),

                    (SELECT COALESCE(SUM(rc.amount), 0)
                     FROM RetailerCommission rc
                     WHERE rc.retailerName = r.retailerName
                       AND rc.date < :startDate)
                )
                FROM RetailerInfo r
                LEFT JOIN ProductStock ps
                    ON ps.customer = r.retailerName
                   AND ps.date < :startDate
                WHERE r.status = 'Active'
                GROUP BY r.category, r.retailerName
                ORDER BY r.category ASC
            """)
    List<CategoryBalanceDTO> findCategoryOpeningBalanceBeforeDate(LocalDate startDate);

    @Query("""
            SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                r.salesPerson,
                COALESCE(SUM(ps.productQty), 0),
                COALESCE(SUM(ps.productQty * ps.dpRate), 0),
                (SELECT COALESCE(SUM(rp.amount), 0)
                    FROM RetailerPayment rp
                    WHERE rp.retailerName = r.retailerName
                    AND FUNCTION('YEAR', rp.date) = FUNCTION('YEAR', CURRENT_DATE)
                    AND FUNCTION('MONTH', rp.date) = FUNCTION('MONTH', CURRENT_DATE)),
                (SELECT COALESCE(SUM(rc.amount), 0)
                    FROM RetailerCommission rc
                    WHERE rc.retailerName = r.retailerName
                    AND FUNCTION('YEAR', rc.date) = FUNCTION('YEAR', CURRENT_DATE)
                    AND FUNCTION('MONTH', rc.date) = FUNCTION('MONTH', CURRENT_DATE))
            )
            FROM RetailerInfo r
            LEFT JOIN ProductStock ps
                ON ps.customer = r.retailerName
                AND FUNCTION('YEAR', ps.date) = FUNCTION('YEAR', CURRENT_DATE)
                AND FUNCTION('MONTH', ps.date) = FUNCTION('MONTH', CURRENT_DATE)
            WHERE r.category = :category AND r.status = 'Active'
            GROUP BY r.salesPerson, r.retailerName order by r.salesPerson asc
            """)
    List<CategoryBalanceDTO> findMarketingBalanceForCurrentMonth(@Param("category") String category);

    @Query("""
                SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                    r.salesPerson,
                    COALESCE(SUM(ps.productQty), 0),
                    COALESCE(SUM(ps.productQty * ps.dpRate), 0),

                    (SELECT COALESCE(SUM(rp.amount), 0)
                     FROM RetailerPayment rp
                     WHERE rp.retailerName = r.retailerName
                       AND rp.date BETWEEN :startDate AND :endDate),

                    (SELECT COALESCE(SUM(rc.amount), 0)
                     FROM RetailerCommission rc
                     WHERE rc.retailerName = r.retailerName
                       AND rc.date BETWEEN :startDate AND :endDate)
                )
                FROM RetailerInfo r
                LEFT JOIN ProductStock ps
                    ON ps.customer = r.retailerName
                   AND ps.date BETWEEN :startDate AND :endDate
                WHERE r.category = :category
                  AND r.status = 'Active'
                GROUP BY r.salesPerson
                ORDER BY r.salesPerson ASC
            """)
    List<CategoryBalanceDTO> findDatewiseMarketingBalance(
            @Param("category") String category,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                r.retailerName,
                COALESCE(SUM(ps.productQty), 0),
                COALESCE(SUM(ps.productQty * ps.dpRate), 0),
                (SELECT COALESCE(SUM(rp.amount), 0)
                    FROM RetailerPayment rp
                    WHERE rp.retailerName = r.retailerName
                    AND FUNCTION('YEAR', rp.date) = FUNCTION('YEAR', CURRENT_DATE)
                    AND FUNCTION('MONTH', rp.date) = FUNCTION('MONTH', CURRENT_DATE)),
                (SELECT COALESCE(SUM(rc.amount), 0)
                    FROM RetailerCommission rc
                    WHERE rc.retailerName = r.retailerName
                    AND FUNCTION('YEAR', rc.date) = FUNCTION('YEAR', CURRENT_DATE)
                    AND FUNCTION('MONTH', rc.date) = FUNCTION('MONTH', CURRENT_DATE))
            )
            FROM RetailerInfo r
            LEFT JOIN ProductStock ps
                ON ps.customer = r.retailerName
                AND FUNCTION('YEAR', ps.date) = FUNCTION('YEAR', CURRENT_DATE)
                AND FUNCTION('MONTH', ps.date) = FUNCTION('MONTH', CURRENT_DATE)
            WHERE r.salesPerson= :salesPerson AND r.status = 'Active'
            GROUP BY r.retailerName order by r.retailerName asc
            """)
    List<CategoryBalanceDTO> findMarketingRetailerBalanceForCurrentMonth(@Param("salesPerson") String salesPerson);

    @Query("""
            SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                r.retailerName,
                COALESCE(SUM(ps.productQty), 0),
                COALESCE(SUM(ps.productQty * ps.dpRate), 0),
                (SELECT COALESCE(SUM(rp.amount), 0)
                    FROM RetailerPayment rp
                    WHERE rp.retailerName = r.retailerName
                    AND rp.date BETWEEN :startDate AND :endDate),
                (SELECT COALESCE(SUM(rc.amount), 0)
                    FROM RetailerCommission rc
                    WHERE rc.retailerName = r.retailerName
                    AND rc.date BETWEEN :startDate AND :endDate)
            )
            FROM RetailerInfo r
            LEFT JOIN ProductStock ps
                ON ps.customer = r.retailerName
                AND ps.date BETWEEN :startDate AND :endDate
            WHERE r.salesPerson= :salesPerson AND r.status = 'Active'
            GROUP BY r.retailerName order by r.retailerName asc
            """)
    List<CategoryBalanceDTO> findDatewiseMarketingRetailerBalance(@Param("salesPerson") String salesPerson,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
                SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                    r.category, 
                    COALESCE(SUM(ps.productQty), 0),
                    COALESCE(SUM(ps.productQty * ps.dpRate), 0),
                    (SELECT COALESCE(SUM(rp.amount), 0)
                        FROM RetailerPayment rp
                        WHERE rp.retailerName = r.retailerName
                        AND (FUNCTION('YEAR', rp.date) < FUNCTION('YEAR', CURRENT_DATE)
                            OR (FUNCTION('YEAR', rp.date) = FUNCTION('YEAR', CURRENT_DATE)
                                AND FUNCTION('MONTH', rp.date) < FUNCTION('MONTH', CURRENT_DATE)))),
                    (SELECT COALESCE(SUM(rc.amount), 0)
                        FROM RetailerCommission rc
                        WHERE rc.retailerName = r.retailerName
                        AND (FUNCTION('YEAR', rc.date) < FUNCTION('YEAR', CURRENT_DATE)
                            OR (FUNCTION('YEAR', rc.date) = FUNCTION('YEAR', CURRENT_DATE)
                                AND FUNCTION('MONTH', rc.date) < FUNCTION('MONTH', CURRENT_DATE))))
                )
                FROM RetailerInfo r
                LEFT JOIN ProductStock ps
                    ON ps.customer = r.retailerName
                    AND (FUNCTION('YEAR', ps.date) < FUNCTION('YEAR', CURRENT_DATE)
                        OR (FUNCTION('YEAR', ps.date) = FUNCTION('YEAR', CURRENT_DATE)
                            AND FUNCTION('MONTH', ps.date) < FUNCTION('MONTH', CURRENT_DATE)))
                WHERE r.status = 'Active'
                GROUP BY r.category, r.retailerName
                ORDER BY r.category ASC
            """)
    List<CategoryBalanceDTO> findCategoryOpeningBalanceBeforeCurrentMonth();

    @Query("""
                SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                    r.salesPerson,
                    COALESCE(SUM(ps.productQty), 0),
                    COALESCE(SUM(ps.productQty * ps.dpRate), 0),
                    (SELECT COALESCE(SUM(rp.amount), 0)
                        FROM RetailerPayment rp
                        WHERE rp.retailerName = r.retailerName
                        AND (FUNCTION('YEAR', rp.date) < FUNCTION('YEAR', CURRENT_DATE)
                            OR (FUNCTION('YEAR', rp.date) = FUNCTION('YEAR', CURRENT_DATE)
                                AND FUNCTION('MONTH', rp.date) < FUNCTION('MONTH', CURRENT_DATE)))),
                    (SELECT COALESCE(SUM(rc.amount), 0)
                        FROM RetailerCommission rc
                        WHERE rc.retailerName = r.retailerName
                        AND (FUNCTION('YEAR', rc.date) < FUNCTION('YEAR', CURRENT_DATE)
                            OR (FUNCTION('YEAR', rc.date) = FUNCTION('YEAR', CURRENT_DATE)
                                AND FUNCTION('MONTH', rc.date) < FUNCTION('MONTH', CURRENT_DATE))))
                )
                FROM RetailerInfo r
                LEFT JOIN ProductStock ps
                    ON ps.customer = r.retailerName
                    AND (FUNCTION('YEAR', ps.date) < FUNCTION('YEAR', CURRENT_DATE)
                        OR (FUNCTION('YEAR', ps.date) = FUNCTION('YEAR', CURRENT_DATE)
                            AND FUNCTION('MONTH', ps.date) < FUNCTION('MONTH', CURRENT_DATE)))
                WHERE r.category = :category AND r.status = 'Active'
                GROUP BY r.salesPerson, r.retailerName
                ORDER BY r.salesPerson ASC
            """)
    List<CategoryBalanceDTO> findMarketOpeningBalanceBeforeCurrentMonth(@Param("category") String category);

    @Query("""
                SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                    r.salesPerson,
                    COALESCE(SUM(ps.productQty), 0),
                    COALESCE(SUM(ps.productQty * ps.dpRate), 0),
                    (SELECT COALESCE(SUM(rp.amount), 0)
                     FROM RetailerPayment rp
                     WHERE rp.retailerName = r.retailerName
                       AND rp.date < :startDate),
                    (SELECT COALESCE(SUM(rc.amount), 0)
                     FROM RetailerCommission rc
                     WHERE rc.retailerName = r.retailerName
                       AND rc.date < :startDate)
                )
                FROM RetailerInfo r
                LEFT JOIN ProductStock ps
                    ON ps.customer = r.retailerName
                   AND ps.date < :startDate
                WHERE r.category = :category
                  AND r.status = 'Active'
                GROUP BY r.salesPerson
                ORDER BY r.salesPerson ASC
            """)
    List<CategoryBalanceDTO> findMarketOpeningBalanceBeforeDate(
            @Param("category") String category,
            @Param("startDate") LocalDate startDate);

    @Query("""
                SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                    r.retailerName,
                    COALESCE(SUM(ps.productQty), 0),
                    COALESCE(SUM(ps.productQty * ps.dpRate), 0),
                    (SELECT COALESCE(SUM(rp.amount), 0)
                        FROM RetailerPayment rp
                        WHERE rp.retailerName = r.retailerName
                        AND (FUNCTION('YEAR', rp.date) < FUNCTION('YEAR', CURRENT_DATE)
                            OR (FUNCTION('YEAR', rp.date) = FUNCTION('YEAR', CURRENT_DATE)
                                AND FUNCTION('MONTH', rp.date) < FUNCTION('MONTH', CURRENT_DATE)))),
                    (SELECT COALESCE(SUM(rc.amount), 0)
                        FROM RetailerCommission rc
                        WHERE rc.retailerName = r.retailerName
                        AND (FUNCTION('YEAR', rc.date) < FUNCTION('YEAR', CURRENT_DATE)
                            OR (FUNCTION('YEAR', rc.date) = FUNCTION('YEAR', CURRENT_DATE)
                                AND FUNCTION('MONTH', rc.date) < FUNCTION('MONTH', CURRENT_DATE))))
                )
                FROM RetailerInfo r
                LEFT JOIN ProductStock ps
                    ON ps.customer = r.retailerName
                    AND (FUNCTION('YEAR', ps.date) < FUNCTION('YEAR', CURRENT_DATE)
                        OR (FUNCTION('YEAR', ps.date) = FUNCTION('YEAR', CURRENT_DATE)
                            AND FUNCTION('MONTH', ps.date) < FUNCTION('MONTH', CURRENT_DATE)))
                WHERE r.salesPerson= :salesPerson AND r.status = 'Active'
                GROUP BY r.retailerName ORDER BY r.retailerName ASC
            """)
    List<CategoryBalanceDTO> findMarketRetailerOpeningBalanceBeforeCurrentMonth(
            @Param("salesPerson") String salesPerson);

    @Query("""
    SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
        r.retailerName,
        COALESCE(SUM(ps.productQty), 0),
        COALESCE(SUM(ps.productQty * ps.dpRate), 0),

        (SELECT COALESCE(SUM(rp.amount), 0)
         FROM RetailerPayment rp
         WHERE rp.retailerName = r.retailerName
           AND rp.date < :startDate),

        (SELECT COALESCE(SUM(rc.amount), 0)
         FROM RetailerCommission rc
         WHERE rc.retailerName = r.retailerName
           AND rc.date < :startDate)
    )
    FROM RetailerInfo r
    LEFT JOIN ProductStock ps
       ON ps.customer = r.retailerName
      AND ps.date < :startDate
    WHERE r.salesPerson = :salesPerson
      AND r.status = 'Active'
    GROUP BY r.retailerName
    ORDER BY r.retailerName ASC
""")
List<CategoryBalanceDTO> findMarketRetailerOpeningBalanceBeforeDate(
        @Param("salesPerson") String salesPerson,
        @Param("startDate") LocalDate startDate
);


    @Query("""
            SELECT new com.example.bake_boss_backend.dto.CategoryBalanceDTO(
                r.category,
                COALESCE(SUM(ps.productQty), 0),
                COALESCE(SUM(ps.productQty * ps.dpRate), 0),
                (SELECT COALESCE(SUM(rp.amount), 0)
                    FROM RetailerPayment rp
                    WHERE rp.retailerName = r.retailerName
                    AND rp.date BETWEEN :startDate AND :endDate),
                (SELECT COALESCE(SUM(rc.amount), 0)
                    FROM RetailerCommission rc
                    WHERE rc.retailerName = r.retailerName
                    AND rc.date BETWEEN :startDate AND :endDate)
            )
            FROM RetailerInfo r
            LEFT JOIN ProductStock ps
                ON ps.customer = r.retailerName
                AND ps.date BETWEEN :startDate AND :endDate
            WHERE r.status = 'Active'
            GROUP BY r.category, r.retailerName
            ORDER BY r.category ASC
            """)
    List<CategoryBalanceDTO> findCategoryRawBalanceDatewise(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // @Query("SELECT new com.example.bake_boss_backend.dto.RetailerBalanceDTO(" +
    // "r.category, r.areaName, r.retailerName, r.retailerCode, r.salesPerson, " +
    // "COALESCE(SUM(ps.productQty), 0) as totalProductQty, " +
    // "COALESCE(SUM(ps.productQty * ps.dpRate), 0) as totalProductValue, " +
    // "(SELECT COALESCE(SUM(rp.amount), 0) FROM RetailerPayment rp " +
    // " WHERE rp.retailerName = r.retailerName AND rp.date BETWEEN :startDate AND
    // :endDate) as totalPayment, "
    // +
    // "(SELECT COALESCE(SUM(rc.amount), 0) FROM RetailerCommission rc " +
    // " WHERE rc.retailerName = r.retailerName AND rc.date BETWEEN :startDate AND
    // :endDate) as totalCommission) "
    // +
    // "FROM RetailerInfo r " +
    // "LEFT JOIN ProductStock ps ON ps.customer = r.retailerName " +
    // "AND ps.date BETWEEN :startDate AND :endDate " +
    // "WHERE r.status = 'Active' " +
    // "GROUP BY r.category, r.areaName, r.retailerName, r.retailerCode,
    // r.salesPerson ORDER BY r.retailerName")
    // List<RetailerBalanceDTO> findRetailerBalanceBetweenDates(
    // @Param("startDate") LocalDate startDate,
    // @Param("endDate") LocalDate endDate);

    // sales
    @Query("SELECT new com.example.bake_boss_backend.dto.RetailerBalanceDTO(" +
            "r.category, r.areaName, r.retailerName, r.retailerCode, r.salesPerson, " +
            "COALESCE(SUM(ps.productQty), 0) as totalProductQty, " +
            "COALESCE(SUM(ps.productQty * ps.dpRate), 0) as totalProductValue, " +
            "(SELECT COALESCE(SUM(rp.amount), 0) FROM RetailerPayment rp " +
            " WHERE rp.retailerName = r.retailerName AND rp.date BETWEEN :startDate AND :endDate) as totalPayment, "
            +
            "(SELECT COALESCE(SUM(rc.amount), 0) FROM RetailerCommission rc " +
            " WHERE rc.retailerName = r.retailerName AND rc.date BETWEEN :startDate AND :endDate) as totalCommission) "
            +
            "FROM RetailerInfo r " +
            "LEFT JOIN ProductStock ps ON ps.customer = r.retailerName " +
            "AND ps.date BETWEEN :startDate AND :endDate " +
            "WHERE r.status = 'Active' AND r.salesPerson = :salesPerson " +
            "GROUP BY r.category, r.areaName, r.retailerName, r.retailerCode, r.salesPerson ORDER BY r.retailerName")
    List<RetailerBalanceDTO> findSalesRetailerBalanceBetweenDates(
            @Param("salesPerson") String salesPerson,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(rp.amount) FROM RetailerPayment rp " +
            "WHERE rp.retailerName IN " +
            "(SELECT ri.retailerName FROM RetailerInfo ri WHERE ri.salesPerson = :employeeName) " +
            "AND EXTRACT(YEAR FROM rp.date) = :year " +
            "AND EXTRACT(MONTH FROM rp.date) = :month")
    Double getPaymentValueForEmployee(@Param("employeeName") String employeeName, @Param("year") int year,
            @Param("month") int month);

    @Query("SELECT SUM(rp.amount) FROM RetailerPayment rp WHERE rp.retailerName IN :retailerNames AND YEAR(rp.date) = :year AND MONTH(rp.date) = :month")
    Double getPaymentValueForRetailers(@Param("retailerNames") List<String> retailerNames, @Param("year") int year, @Param("month") int month);

    @Query("""
    SELECT COALESCE(SUM(r.amount), 0)
    FROM RetailerPayment r
    """)
Double getTotalRetailerPayment();

}
