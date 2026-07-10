package com.capnex_backend.service;

import com.capnex_backend.entity.Alerte;
import com.capnex_backend.repository.AlerteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlerteService {

    private final AlerteRepository alerteRepository;
    private final WebSocketService webSocketService;

    private static final String[] SIZING_LABELS = {"", "0X", "1X", "2X", "3X"};

    public void creerAlerteChangementSignal(String ticker, int ancienSizing, int nouveauSizing) {
        String ancienLabel = SIZING_LABELS[Math.min(ancienSizing, 4)];
        String nouveauLabel = SIZING_LABELS[Math.min(nouveauSizing, 4)];

        Alerte alerte = new Alerte();
        alerte.setTicker(ticker);
        alerte.setAncienSizing(ancienSizing);
        alerte.setNouveauSizing(nouveauSizing);

        if (nouveauSizing == 1) {
            alerte.setType(Alerte.Type.FAIBLE);
            alerte.setMessage(ticker + " — Signal passé à 0X FAIBLE · Position à surveiller");
        } else {
            alerte.setType(Alerte.Type.SIGNAL);
            alerte.setMessage(ticker + " — Signal changé : " + ancienLabel + " → " + nouveauLabel);
        }

        alerteRepository.save(alerte);

        // Push WebSocket
        webSocketService.envoyerAlerte(ticker, nouveauSizing);

        log.info("🔔 Alerte créée : {}", alerte.getMessage());
    }

    public List<Alerte> getAllAlertes() {
        return alerteRepository.findAllByOrderByCreatedAtDesc();
    }

    public long getNonLues() {
        return alerteRepository.countByLuFalse();
    }

    public void marquerLu(Long id) {
        alerteRepository.findById(id).ifPresent(a -> {
            a.setLu(true);
            alerteRepository.save(a);
        });
    }

    public void toutMarquerLu() {
        List<Alerte> alertes = alerteRepository.findByLuFalseOrderByCreatedAtDesc();
        alertes.forEach(a -> a.setLu(true));
        alerteRepository.saveAll(alertes);
    }

    public void supprimer(Long id) {
        alerteRepository.deleteById(id);
    }

    public void creerAlerteStopLoss(String ticker, double prixActuel, double slDefini) {
    // Éviter les doublons — vérifier si une alerte SL existe déjà aujourd'hui
    boolean dejaAlerte = alerteRepository.existsByTickerAndTypeAndCreatedAtAfter(
        ticker, 
        Alerte.Type.STOP_LOSS, 
        LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)
    );

    if (dejaAlerte) return;

    Alerte alerte = new Alerte();
    alerte.setTicker(ticker);
    alerte.setType(Alerte.Type.STOP_LOSS);
    alerte.setMessage(String.format(
        "%s — ⚠ STOP LOSS ATTEINT · Prix actuel %s XOF ≤ SL %s XOF",
        ticker,
        String.format("%,.0f", prixActuel),
        String.format("%,.0f", slDefini)
    ));
    alerte.setNouveauSizing(0);
    alerteRepository.save(alerte);

    // Push WebSocket
    webSocketService.envoyerAlerte(ticker, 0);

    log.info("🛑 Stop Loss atteint : {} — Prix {} ≤ SL {}", ticker, prixActuel, slDefini);
}
}
