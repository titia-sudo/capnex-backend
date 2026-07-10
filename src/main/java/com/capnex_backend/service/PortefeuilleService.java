package com.capnex_backend.service;

import com.capnex_backend.dto.PortefeuilleRequest;
import com.capnex_backend.dto.PositionRequest;
import com.capnex_backend.entity.Actif;
import com.capnex_backend.entity.Portefeuille;
import com.capnex_backend.entity.Position;
import com.capnex_backend.entity.Trade;
import com.capnex_backend.entity.User;
import com.capnex_backend.repository.ActifRepository;
import com.capnex_backend.repository.PortefeuilleRepository;
import com.capnex_backend.repository.PositionRepository;
import com.capnex_backend.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortefeuilleService {

    private final PositionRepository positionRepository;
    private final ActifRepository actifRepository;
    private final TradeRepository tradeRepository;
    private final PortefeuilleRepository portefeuilleRepository;

    // ── PORTEFEUILLES ──
    public List<Portefeuille> getPortefeuilles(Long userId) {
        return portefeuilleRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Portefeuille creerPortefeuille(User user, PortefeuilleRequest request) {
        Portefeuille p = new Portefeuille();
        p.setNom(request.getNom());
        p.setUser(user);
        p.setCapitalInitial(request.getCapitalInitial());
        Portefeuille saved = portefeuilleRepository.save(p);

        // Créer les positions associées
        if (request.getPositions() != null) {
            for (PositionRequest pr : request.getPositions()) {
                ajouterPositionDansPortefeuille(user, saved, pr);
            }
        }
        return saved;
    }

    public void supprimerPortefeuille(Long id, Long userId) {
        Portefeuille p = portefeuilleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portefeuille non trouvé"));
        if (!p.getUser().getId().equals(userId))
            throw new RuntimeException("Non autorisé");
        positionRepository.findByPortefeuilleId(id)
                .forEach(positionRepository::delete);
        portefeuilleRepository.delete(p);
    }

    // ── POSITIONS ──
    public List<Position> getPositions(Long userId) {
        return positionRepository.findByUserId(userId);
    }

    public List<Position> getPositionsByPortefeuille(Long portefeuilleId) {
        return positionRepository.findByPortefeuilleId(portefeuilleId);
    }

    private void ajouterPositionDansPortefeuille(User user, Portefeuille portefeuille, PositionRequest request) {
        actifRepository.findByTicker(request.getTicker())
                .orElseThrow(() -> new RuntimeException("Actif non trouvé : " + request.getTicker()));

        Position position = new Position();
        position.setUser(user);
        position.setPortefeuille(portefeuille);
        position.setTicker(request.getTicker());
        position.setLots(request.getLots());
        position.setPrixEntree(request.getPrixEntree());
        position.setDateEntree(LocalDateTime.now());
        positionRepository.save(position);
    }

    public Position ajouterPosition(User user, PositionRequest request) {
        actifRepository.findByTicker(request.getTicker())
                .orElseThrow(() -> new RuntimeException("Actif non trouvé"));

        Position position = new Position();
        position.setUser(user);
        position.setTicker(request.getTicker());
        position.setLots(request.getLots());
        position.setPrixEntree(request.getPrixEntree());
        position.setDateEntree(LocalDateTime.now());

        // Lier au portefeuille si fourni
        if (request.getPortefeuilleId() != null) {
            portefeuilleRepository.findById(request.getPortefeuilleId())
                    .ifPresent(position::setPortefeuille);
        }

        return positionRepository.save(position);
    }

    @Transactional
    public void supprimerPosition(Long id, Long userId) {
        positionRepository.deleteByIdAndUserId(id, userId);
    }

    @Transactional
    public Map<String, Object> cloturerPosition(Long id, Long userId, Double prixSortie) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position non trouvée"));

        if (!position.getUser().getId().equals(userId))
            throw new RuntimeException("Non autorisé");

        Actif actif = actifRepository.findByTicker(position.getTicker())
                .orElseThrow(() -> new RuntimeException("Actif non trouvé"));

        double capital = position.getLots() * position.getPrixEntree();
        double perf = ((prixSortie - position.getPrixEntree()) / position.getPrixEntree()) * 100;
        double pnl = (perf / 100) * capital;

        String sizingLabel = actif.getSizing() == 4 ? "3X" :
                             actif.getSizing() == 3 ? "2X" :
                             actif.getSizing() == 2 ? "1X" : "0X";

        Trade trade = new Trade();
        trade.setUser(position.getUser());
        trade.setTicker(position.getTicker());
        trade.setDateIn(position.getDateEntree().toLocalDate());
        trade.setPrixIn(position.getPrixEntree());
        trade.setPrixOut(prixSortie);
        trade.setCapital(capital);
        trade.setSizing(sizingLabel);
        trade.setStatut(Trade.Statut.CLOSED);
        trade.setPnl(pnl);
        trade.setPerf(perf);
        tradeRepository.save(trade);

        positionRepository.delete(position);

        Map<String, Object> result = new HashMap<>();
        result.put("ticker", position.getTicker());
        result.put("lots", position.getLots());
        result.put("prixEntree", position.getPrixEntree());
        result.put("prixSortie", prixSortie);
        result.put("capital", capital);
        result.put("pnl", pnl);
        result.put("perf", perf);
        result.put("sizing", sizingLabel);
        result.put("message", "Position clôturée — Trade créé automatiquement");
        return result;
    }
}