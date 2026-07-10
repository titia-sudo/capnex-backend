package com.capnex_backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capnex_backend.entity.Actif;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActifRepository extends JpaRepository<Actif, Long> {
    Optional<Actif> findByTicker(String ticker);
    List<Actif> findAllByOrderByScoreDesc();
    List<Actif> findBySizing(Integer sizing);
    boolean existsByTicker(String ticker);
}
