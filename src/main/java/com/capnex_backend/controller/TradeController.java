package com.capnex_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.capnex_backend.dto.TradeRequest;
import com.capnex_backend.entity.Trade;
import com.capnex_backend.entity.User;
import com.capnex_backend.service.TradeService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @GetMapping
    public ResponseEntity<List<Trade>> getTrades(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tradeService.getTrades(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Trade> ajouterTrade(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TradeRequest request) {
        return ResponseEntity.ok(tradeService.ajouterTrade(user, request));
    }

    @PutMapping("/{id}/cloturer")
    public ResponseEntity<Trade> cloturerTrade(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(tradeService.cloturerTrade(id, user.getId(), body.get("prixOut")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerTrade(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        tradeService.supprimerTrade(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
