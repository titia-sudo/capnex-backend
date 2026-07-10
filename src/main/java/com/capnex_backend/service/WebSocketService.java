package com.capnex_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void envoyerPrix(Map<String, Double> prix) {
        HashMap<String, Double> payload = new HashMap<>(prix);
        messagingTemplate.convertAndSend("/topic/prix", (Object) payload);
        log.info("WebSocket — {} prix envoyés", prix.size());
    }

    public void envoyerAlerte(String ticker, int sizing) {
        HashMap<String, Object> alerte = new HashMap<>();
        alerte.put("ticker", ticker);
        alerte.put("sizing", sizing);
        alerte.put("type", sizing == 0 ? "SORTIR" : "SIGNAL");
        alerte.put("message", sizing == 0
                ? ticker + " — Sizing 0X · Sortir immédiatement"
                : ticker + " — Sizing changé à " + sizing + "X");
        messagingTemplate.convertAndSend("/topic/alertes", (Object) alerte);
        log.info("WebSocket — Alerte envoyée : {}", alerte);
    }
}