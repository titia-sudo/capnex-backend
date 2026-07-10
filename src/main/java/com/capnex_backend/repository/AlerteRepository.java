package com.capnex_backend.repository;

import com.capnex_backend.entity.Alerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Long> {
    List<Alerte> findAllByOrderByCreatedAtDesc();
    List<Alerte> findByLuFalseOrderByCreatedAtDesc();
    long countByLuFalse();
    boolean existsByTickerAndTypeAndCreatedAtAfter(String ticker, Alerte.Type type, LocalDateTime date);
}
