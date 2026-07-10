package com.capnex_backend.controller;

import com.capnex_backend.entity.Alerte;
import com.capnex_backend.service.AlerteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alertes")
@RequiredArgsConstructor
public class AlerteController {

    private final AlerteService alerteService;

    @GetMapping
    public ResponseEntity<List<Alerte>> getAllAlertes() {
        return ResponseEntity.ok(alerteService.getAllAlertes());
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getNonLues() {
        return ResponseEntity.ok(Map.of("nonLues", alerteService.getNonLues()));
    }

    @PutMapping("/{id}/lu")
    public ResponseEntity<Void> marquerLu(@PathVariable Long id) {
        alerteService.marquerLu(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/tout-lu")
    public ResponseEntity<Void> toutMarquerLu() {
        alerteService.toutMarquerLu();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        alerteService.supprimer(id);
        return ResponseEntity.ok().build();
    }
}
