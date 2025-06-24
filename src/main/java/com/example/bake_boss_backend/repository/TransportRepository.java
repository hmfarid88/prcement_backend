package com.example.bake_boss_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bake_boss_backend.entity.Transport;

public interface TransportRepository extends JpaRepository<Transport, Long>{

    boolean existsByUsernameAndTransport(String username, String transport);

    List<Transport> getTransportByUsername(String username);

    void deleteByTransport(String transport);

}
