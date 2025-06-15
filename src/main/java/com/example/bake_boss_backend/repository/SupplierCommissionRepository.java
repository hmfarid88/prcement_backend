package com.example.bake_boss_backend.repository;

import java.util.List;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bake_boss_backend.dto.SupplierDetailsDTO;
import com.example.bake_boss_backend.entity.SupplierCommission;

public interface SupplierCommissionRepository extends JpaRepository<SupplierCommission, Long> {

     @Query("SELECT o FROM SupplierCommission o WHERE YEAR(o.date) = :year AND MONTH(o.date) = :month AND o.username = :username ORDER BY o.date")
  List<SupplierCommission> findSupplierCommissionByMonth(@Param("year") int year, @Param("month") int month, @Param("username") String username);

  @Query("SELECT o FROM SupplierCommission o WHERE o.username = :username AND  o.date BETWEEN :startDate AND :endDate ORDER BY o.date")
  List<SupplierCommission> findSupplierCommissionByDate(String username, LocalDate startDate, LocalDate endDate);

  @Query("SELECT sp.supplierName, SUM(sp.amount) " +
      "FROM SupplierCommission sp WHERE sp.username = :username GROUP BY sp.supplierName")
  List<Object[]> findTotalCommissionGroupedBySupplierAndUsername(String username);

   @Query("SELECT new com.example.bake_boss_backend.dto.SupplierDetailsDTO(rp.date, 'No', 0.0, 0.0, 0.0, SUM(rp.amount)) FROM SupplierCommission rp WHERE rp.username = :username AND  rp.supplierName = :supplierName  GROUP BY rp.date")
       List<SupplierDetailsDTO> findCommissionDetailsByUsernameAndSupplierName(String username, String supplierName);
}
