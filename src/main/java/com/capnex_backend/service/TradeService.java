package com.capnex_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.capnex_backend.dto.TradeRequest;
import com.capnex_backend.entity.Trade;
import com.capnex_backend.entity.User;
import com.capnex_backend.repository.TradeRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    public List<Trade> getTrades(Long userId) {
        return tradeRepository.findByUserId(userId);
    }

    public Trade ajouterTrade(User user, TradeRequest request) {
        Trade trade = new Trade();
        trade.setUser(user);
        trade.setTicker(request.getTicker());
        trade.setPrixIn(request.getPrixIn());
        trade.setCapital(request.getCapital());
        trade.setSizing(request.getSizing());
        trade.setStatut(Trade.Statut.OPEN);
        trade.setDateIn(request.getDateIn() != null
                ? LocalDate.parse(request.getDateIn())
                : LocalDate.now());

        return tradeRepository.save(trade);
    }

    public Trade cloturerTrade(Long id, Long userId, Double prixOut) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade non trouvé"));

        if (!trade.getUser().getId().equals(userId)) {
            throw new RuntimeException("Non autorisé");
        }

        trade.setPrixOut(prixOut);
        trade.setStatut(Trade.Statut.CLOSED);

        double perf = ((prixOut - trade.getPrixIn()) / trade.getPrixIn()) * 100;
        double pnl = (perf / 100) * trade.getCapital();
        trade.setPerf(perf);
        trade.setPnl(pnl);

        return tradeRepository.save(trade);
    }

    public void supprimerTrade(Long id, Long userId) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trade non trouvé"));

        if (!trade.getUser().getId().equals(userId)) {
            throw new RuntimeException("Non autorisé");
        }

        tradeRepository.delete(trade);
    }
}
