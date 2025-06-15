package com.example.bake_boss_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bake_boss_backend.dto.TransportDetailsDTO;
import com.example.bake_boss_backend.entity.TransportPayment;

public interface TransportPaymentRepository extends JpaRepository<TransportPayment, Long>{
    @Query("SELECT sp.transport, SUM(sp.amount) " +
      "FROM TransportPayment sp WHERE sp.username = :username GROUP BY sp.transport")
  List<Object[]> findTotalPaymentGroupedByTransportAndUsername(String username);

  @Query("SELECT new com.example.bake_boss_backend.dto.TransportDetailsDTO(rp.date, 'No', 0.0, 0.0, SUM(rp.amount)) FROM TransportPayment rp WHERE rp.username = :username AND  rp.transport = :transport GROUP BY rp.date")
  List<TransportDetailsDTO> findPaymentDetailsByUsernameAndTransport(String username, String transport);

  @Query("SELECT o FROM TransportPayment o WHERE YEAR(o.date) = :year AND MONTH(o.date) = :month AND o.username = :username ORDER BY o.date")
  List<TransportPayment> findPaymentsByMonth(@Param("year") int year, @Param("month") int month, @Param("username") String username);

  @Query("SELECT o FROM TransportPayment o WHERE o.username = :username AND  o.date BETWEEN :startDate AND :endDate ORDER BY o.date")
  List<TransportPayment> findPaymentsByDate(String username, LocalDate startDate, LocalDate endDate);
}
