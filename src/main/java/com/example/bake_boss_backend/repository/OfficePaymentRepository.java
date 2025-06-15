package com.example.bake_boss_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bake_boss_backend.dto.DetailsPayReceiveDTO;
import com.example.bake_boss_backend.dto.PaymentDto;
import com.example.bake_boss_backend.entity.OfficePayment;

public interface OfficePaymentRepository extends JpaRepository<OfficePayment, Long> {
    @Query(value = "SELECT new com.example.bake_boss_backend.dto.PaymentDto(p.date, p.paymentName, p.paymentNote, p.amount) "
            + "FROM OfficePayment p WHERE p.username=:username AND p.date = :date")
    List<PaymentDto> findPaymentsForToday(@Param("username") String username, @Param("date") LocalDate date);

    @Query("SELECT o FROM OfficePayment o WHERE YEAR(o.date) = :year AND MONTH(o.date) = :month AND o.username = :username ORDER BY o.date")
    List<OfficePayment> findPaymentsByMonth(@Param("year") int year, @Param("month") int month, @Param("username") String username);

    @Query("SELECT o FROM OfficePayment o WHERE o.username = :username AND  o.date BETWEEN :startDate AND :endDate ORDER BY o.date")
    List<OfficePayment> findPaymentsByDate(String username, LocalDate startDate, LocalDate endDate);

    @Query("SELECT p.paymentName, SUM(p.amount) FROM OfficePayment p GROUP BY p.paymentName")
    List<Object[]> findTotalPaymentAmountGroupedByPaymentName();

    @Query("SELECT new com.example.bake_boss_backend.dto.DetailsPayReceiveDTO(p.date, p.paymentNote, p.amount, 0.0) FROM OfficePayment p where p.username= :username AND p.paymentName= :paymentName ORDER BY p.date ASC")
    List<DetailsPayReceiveDTO> findDetailsPaymentByPaymentNameAndUsername(@Param("username") String username, @Param("paymentName") String name);
   


}
