package com.example.bake_boss_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bake_boss_backend.entity.ExpenseName;

public interface ExpenseNameRepository extends JpaRepository<ExpenseName, Long>{

    boolean existsByUsernameAndExpenseName(String username, String expenseName);

    void deleteByExpenseName(String expenseName);

    List<ExpenseName> findByUsername(String username);

    
} 