package com.example.bake_boss_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bake_boss_backend.dto.PaymentDto;
import com.example.bake_boss_backend.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long>{

     @Query("SELECT new com.example.bake_boss_backend.dto.PaymentDto(e.date, e.expenseName, e.expenseNote, e.amount) "
            + "FROM Expense e WHERE e.username = :username AND e.date = :date")
    List<PaymentDto> findExpenseForToday(@Param("username") String username, @Param("date") LocalDate date);

    @Query("SELECT e FROM Expense e WHERE MONTH(e.date) = :month AND YEAR(e.date) = :year AND e.username = :username ORDER BY e.date")
    List<Expense> findByMonthYearAndUsername(@Param("month") int month, @Param("year") int year, @Param("username") String username);

    @Query("SELECT e FROM Expense e WHERE e.username = :username AND  e.date BETWEEN :startDate AND :endDate  ORDER BY e.date")
    List<Expense> findDatewiseExpense(String username, LocalDate startDate, LocalDate endDate);
}
