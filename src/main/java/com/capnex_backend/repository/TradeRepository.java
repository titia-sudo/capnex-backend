package com.capnex_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capnex_backend.entity.Trade;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByUserId(Long userId);
    List<Trade> findByUserIdAndStatut(Long userId, Trade.Statut statut);
}
