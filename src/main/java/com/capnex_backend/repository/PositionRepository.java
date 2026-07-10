package com.capnex_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capnex_backend.entity.Position;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByUserId(Long userId);
    void deleteByIdAndUserId(Long id, Long userId);
    List<Position> findByPortefeuilleId(Long portefeuilleId);
}
