package com.capnex_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.capnex_backend.dto.ActifUpdateRequest;
import com.capnex_backend.entity.Actif;
import com.capnex_backend.repository.ActifRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActifService {

    private final ActifRepository actifRepository;

    public List<Actif> getAllActifs() {
        return actifRepository.findAllByOrderByScoreDesc();
    }

    public List<Actif> getActifsBySizing(Integer sizing) {
        return actifRepository.findBySizing(sizing);
    }

    public Actif getActifByTicker(String ticker) {
        return actifRepository.findByTicker(ticker)
                .orElseThrow(() -> new RuntimeException("Actif non trouvé : " + ticker));
    }

    public Actif updateActif(String ticker, ActifUpdateRequest request) {
        Actif actif = getActifByTicker(ticker);

        if (request.getPrix() != null) actif.setPrix(request.getPrix());
        if (request.getLiq() != null) actif.setLiq(request.getLiq());
        if (request.getCa() != null) actif.setCa(request.getCa());
        if (request.getCroiss() != null) actif.setCroiss(request.getCroiss());
        if (request.getPer5() != null) actif.setPer5(request.getPer5());
        if (request.getNote() != null) actif.setNote(request.getNote());
        if (request.getMarge() != null) actif.setMarge(request.getMarge());
        if (request.getFreq() != null) actif.setFreq(request.getFreq());
        if (request.getNotediv() != null) actif.setNotediv(request.getNotediv());
        if (request.getSigM() != null) actif.setSigM(request.getSigM());
        if (request.getSigW() != null) actif.setSigW(request.getSigW());
        if (request.getSigD() != null) actif.setSigD(request.getSigD());
        if (request.getDailyM() != null) actif.setDailyM(request.getDailyM());
        if (request.getEntree() != null) actif.setEntree(request.getEntree());
        if (request.getSl() != null) actif.setSl(request.getSl());
        if (request.getTp() != null) actif.setTp(request.getTp());

        // Recalcul score
        int score = calculerScore(actif);
        actif.setScore(score);
        actif.setSizing(calculerSizing(score, actif.getSigM(), actif.getLiq()));

        return actifRepository.save(actif);
    }

    public int calculerScore(Actif a) {
        double liq = a.getLiq() != null ? a.getLiq() : 0;
        double ca = a.getCa() != null ? a.getCa() : 0;
        double croiss = a.getCroiss() != null ? a.getCroiss() : 0;
        double marge = a.getMarge() != null ? a.getMarge() : 0;
        double per5 = a.getPer5() != null ? a.getPer5() : 0;
        double freq = a.getFreq() != null ? a.getFreq() : 0;
        double notediv = a.getNotediv() != null ? a.getNotediv() : 0;

        double note = ((liq + ca + croiss + marge + per5 + freq) / 6.0) * 10.0;
        double global = (note * 0.7) + (notediv * 0.3);

        a.setNote((int) Math.round(note));

        return (int) Math.round(global);
    }

    public int calculerSizing(int score, String sigM, Integer liq) {
        if (score > 80) return 4;
        if (score >= 60) return 3;
        if (score >= 40) return 2;
        return 1;
    }
}
