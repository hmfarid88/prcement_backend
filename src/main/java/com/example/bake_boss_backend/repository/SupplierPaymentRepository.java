package com.example.bake_boss_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bake_boss_backend.dto.PaymentDto;
import com.example.bake_boss_backend.dto.SupplierDetailsDTO;
import com.example.bake_boss_backend.entity.SupplierPayment;

public interface SupplierPaymentRepository extends JpaRepository<SupplierPayment, Long> {
  @Query("SELECT new com.example.bake_boss_backend.dto.PaymentDto(s.date, s.supplierName, s.note, s.amount) "
      + "FROM SupplierPayment s WHERE s.username = :username AND s.date = :date")
  List<PaymentDto> findSupplierPaymentsForToday(@Param("username") String username, @Param("date") LocalDate date);

  @Query("SELECT o FROM SupplierPayment o WHERE YEAR(o.date) = :year AND MONTH(o.date) = :month AND o.username = :username ORDER BY o.date")
  List<SupplierPayment> findPaymentsByMonth(@Param("year") int year, @Param("month") int month, @Param("username") String username);

  @Query("SELECT o FROM SupplierPayment o WHERE o.username = :username AND  o.date BETWEEN :startDate AND :endDate ORDER BY o.date")
  List<SupplierPayment> findPaymentsByDate(String username, LocalDate startDate, LocalDate endDate);

  @Query("SELECT sp.supplierName, SUM(sp.amount) " +
      "FROM SupplierPayment sp WHERE sp.username = :username GROUP BY sp.supplierName")
  List<Object[]> findTotalPaymentGroupedBySupplierAndUsername(String username);

  @Query("SELECT new com.example.bake_boss_backend.dto.SupplierDetailsDTO(rp.date, 'No', 0.0, 0.0, SUM(rp.amount), 0.0) FROM SupplierPayment rp WHERE rp.username = :username AND  rp.supplierName = :supplierName GROUP BY rp.date")
  List<SupplierDetailsDTO> findPaymentDetailsByUsernameAndSupplierName(String username, String supplierName);
}
