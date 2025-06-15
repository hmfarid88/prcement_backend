package com.example.bake_boss_backend.repository;

import java.util.List;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bake_boss_backend.dto.PaymentDto;
import com.example.bake_boss_backend.entity.EmployeePayment;

public interface EmployeePaymentRepository extends JpaRepository<EmployeePayment, Long> {

    @Query("SELECT e FROM EmployeePayment e WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month AND e.username = :username")
    List<EmployeePayment> findByMonthYearAndUsername(@Param("year") int year, @Param("month") int month, @Param("username") String username);

    @Query("SELECT e FROM EmployeePayment e WHERE e.username = :username AND  e.date BETWEEN :startDate AND :endDate")
    List<EmployeePayment> findDatewiseEmployeePayment(String username, LocalDate startDate, LocalDate endDate);

    @Query("SELECT new com.example.bake_boss_backend.dto.PaymentDto(s.date, s.employeeName, s.note, s.amount) "
      + "FROM EmployeePayment s WHERE s.username = :username AND s.date = :date")
  List<PaymentDto> findEmployeePaymentsForToday(@Param("username") String username, @Param("date") LocalDate date);

    @Query("SELECT SUM(e.amount) " +
            "FROM EmployeePayment e " +
            "WHERE e.username = :username " +
            "AND e.employeeName = :employeeName " +
            "AND e.year = :year " +
            "AND e.month = :month")
    Double findSumOfAmountByEmployeeNameYearMonthAndUsername(@Param("username") String username, @Param("employeeName") String employeeName, @Param("year") int year, @Param("month") int month);
}
