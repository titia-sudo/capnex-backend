package com.capnex_backend.repository;

import com.capnex_backend.entity.PrixHistorique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrixHistoriqueRepository extends JpaRepository<PrixHistorique, Long> {
    List<PrixHistorique> findByTickerOrderByDateCotationAsc(String ticker);
    List<PrixHistorique> findByTickerAndDateCotationBetweenOrderByDateCotationAsc(
        String ticker, LocalDate debut, LocalDate fin);
    Optional<PrixHistorique> findByTickerAndDateCotation(String ticker, LocalDate date);
    boolean existsByTickerAndDateCotation(String ticker, LocalDate date);
}
