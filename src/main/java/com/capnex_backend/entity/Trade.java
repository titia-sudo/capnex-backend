package com.capnex_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String ticker;

    @Column(name = "date_in")
    private LocalDate dateIn;

    @Column(name = "prix_in")
    private Double prixIn;

    @Column(name = "prix_out")
    private Double prixOut;

    private Double capital;
    private String sizing;

    @Enumerated(EnumType.STRING)
    private Statut statut;

    private Double pnl;
    private Double perf;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (statut == null) statut = Statut.OPEN;
    }

    public enum Statut {
        OPEN, CLOSED
    }
}
