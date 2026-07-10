package com.capnex_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.capnex_backend.dto.ActifUpdateRequest;
import com.capnex_backend.entity.Actif;
import com.capnex_backend.entity.PrixHistorique;
import com.capnex_backend.repository.PrixHistoriqueRepository;
import com.capnex_backend.service.ActifService;
import com.capnex_backend.service.BrvmScraperService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/actifs")
@RequiredArgsConstructor
public class ActifController {

    private final ActifService actifService;
    private final BrvmScraperService scraperService;
    private final PrixHistoriqueRepository prixHistoriqueRepository;

    @GetMapping
    public ResponseEntity<List<Actif>> getAllActifs() {
        return ResponseEntity.ok(actifService.getAllActifs());
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<Actif> getActif(@PathVariable String ticker) {
        return ResponseEntity.ok(actifService.getActifByTicker(ticker));
    }

    @GetMapping("/sizing/{sizing}")
    public ResponseEntity<List<Actif>> getActifsBySizing(@PathVariable Integer sizing) {
        return ResponseEntity.ok(actifService.getActifsBySizing(sizing));
    }

    @PutMapping("/{ticker}")
    @PreAuthorize("hasRole('ANALYSTE')")
    public ResponseEntity<Actif> updateActif(
            @PathVariable String ticker,
            @RequestBody ActifUpdateRequest request) {
        return ResponseEntity.ok(actifService.updateActif(ticker, request));
    }


    @GetMapping("/{ticker}/historique")
    public ResponseEntity<List<Map<String, Object>>> getHistorique(
            @PathVariable String ticker,
            @RequestParam(defaultValue = "30") int jours) {

        LocalDate debut = LocalDate.now().minusDays(jours);
        LocalDate fin = LocalDate.now();

        List<PrixHistorique> historique = prixHistoriqueRepository
                .findByTickerAndDateCotationBetweenOrderByDateCotationAsc(ticker, debut, fin);

        List<Map<String, Object>> result = historique.stream()
                .map(p -> {
                    Map<String, Object> point = new HashMap<>();
                    point.put("date", p.getDateCotation().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd MMM", java.util.Locale.FRENCH)));
                    point.put("prix", p.getPrix());
                    return point;
                })
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(result);
    }

    
    
}
