package com.capnex_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.capnex_backend.service.BrvmScraperService;

import java.util.Map;

@RestController
@RequestMapping("/api/scraper")
@RequiredArgsConstructor
public class ScraperController {

    private final BrvmScraperService scraperService;

    @PostMapping("/run")
    @PreAuthorize("hasRole('ANALYSTE')")
    public ResponseEntity<Map<String, Object>> runScraper() {
        Map<String, Double> prix = scraperService.scraperMaintenant();;
        return ResponseEntity.ok(Map.of(
            "status", "ok",
            "actifsMisAJour", prix.size(),
            "prix", prix
        ));
    }
}
