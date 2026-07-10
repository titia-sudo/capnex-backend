package com.capnex_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "actifs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Actif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String ticker;

    @Column(nullable = false)
    private String nom;

    private String pays;
    private String secteur;

    // Cours
    private Double prix;
    private Double entree;
    private Double sl;
    private Double tp;

    // Fondamentaux
    private Integer liq;
    private Integer ca;
    private Integer croiss;
    private Double per5;
    private Integer note;
    private Integer marge;
    private Double freq;     // Fréquence cotation 0-10
    private Double notediv;  // Note dividende 0-10

    // Signaux
    @Column(name = "sig_m")
    private String sigM;

    @Column(name = "sig_w")
    private String sigW;

    @Column(name = "sig_d")
    private String sigD;

    @Column(name = "daily_m")
    private Integer dailyM;

    // Calculés
    private Integer score;
    private Integer sizing;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
