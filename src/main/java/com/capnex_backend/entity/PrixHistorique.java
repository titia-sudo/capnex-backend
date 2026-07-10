package com.capnex_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prix_historique", indexes = {
    @Index(name = "idx_ticker_date", columnList = "ticker, date_cotation")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrixHistorique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ticker;

    @Column(nullable = false)
    private Double prix;

    @Column(name = "date_cotation", nullable = false)
    private LocalDate dateCotation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
