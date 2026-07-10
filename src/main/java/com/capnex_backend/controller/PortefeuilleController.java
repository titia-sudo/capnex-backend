package com.capnex_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.capnex_backend.dto.PortefeuilleRequest;
import com.capnex_backend.dto.PositionRequest;
import com.capnex_backend.entity.Portefeuille;
import com.capnex_backend.entity.Position;
import com.capnex_backend.entity.User;
import com.capnex_backend.service.PortefeuilleService;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/portefeuille")
@RequiredArgsConstructor
public class PortefeuilleController {

    private final PortefeuilleService portefeuilleService;

    // ── PORTEFEUILLES ──
    @GetMapping
    public ResponseEntity<List<Portefeuille>> getPortefeuilles(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(portefeuilleService.getPortefeuilles(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Portefeuille> creerPortefeuille(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PortefeuilleRequest request) {
        return ResponseEntity.ok(portefeuilleService.creerPortefeuille(user, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerPortefeuille(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        portefeuilleService.supprimerPortefeuille(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ── POSITIONS PAR PORTEFEUILLE ──
    @GetMapping("/{id}/positions")
    public ResponseEntity<List<Position>> getPositions(@PathVariable Long id) {
        return ResponseEntity.ok(portefeuilleService.getPositionsByPortefeuille(id));
    }

    // ── POSITION INDIVIDUELLE ──
    @PostMapping("/position")
    public ResponseEntity<Position> ajouterPosition(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PositionRequest request) {
        return ResponseEntity.ok(portefeuilleService.ajouterPosition(user, request));
    }

    @DeleteMapping("/position/{id}")
    public ResponseEntity<Void> supprimerPosition(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        portefeuilleService.supprimerPosition(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/position/{id}/cloturer")
    public ResponseEntity<Map<String, Object>> cloturerPosition(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        return ResponseEntity.ok(
            portefeuilleService.cloturerPosition(id, user.getId(), body.get("prixSortie"))
        );
    }
}