package com.capnex_backend.repository;

import com.capnex_backend.entity.Portefeuille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PortefeuilleRepository extends JpaRepository<Portefeuille, Long> {
    List<Portefeuille> findByUserIdOrderByCreatedAtDesc(Long userId);
}
