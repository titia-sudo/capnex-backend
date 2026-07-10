package com.capnex_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.capnex_backend.entity.Actif;
import com.capnex_backend.repository.ActifRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ActifRepository actifRepository;

    @Override
    public void run(String... args) {
        List<Actif> tousLesActifs = getTousLesActifs();
        for (Actif a : tousLesActifs) {
            if (!actifRepository.existsByTicker(a.getTicker())) {
                actifRepository.save(a);
                System.out.println("✅ Actif ajouté : " + a.getTicker());
            }
        }
        System.out.println("✅ " + actifRepository.count() + " actifs en base");
    }

    private List<Actif> getTousLesActifs() {
        return List.of(
            actif("SNTS", "Sonatel", "SN", "Telecom", 15000.0, 14500.0, 13000.0, 18000.0, 9, 9, 9, 9, 9, 9, 8, "oui", "oui", "oui", 3),
            actif("SGBC", "Société Générale CI", "CI", "Banque", 14500.0, 13000.0, 12000.0, 17000.0, 8, 8, 8, 8, 8, 8, 7, "oui", "oui", "oui", 3),
            actif("SLBC", "Solibra CI", "CI", "Industrie", 105000.0, 100000.0, 92000.0, 120000.0, 7, 8, 7, 8, 7, 8, 6, "oui", "moyen", "oui", 2),
            actif("BICC", "BICI CI", "CI", "Banque", 8500.0, 8000.0, 7200.0, 10000.0, 7, 7, 6, 7, 6, 8, 5, "oui", "moyen", "non", 2),
            actif("CBIBF", "Coris Bank BF", "BF", "Banque", 8900.0, 8500.0, 7800.0, 10500.0, 7, 7, 7, 7, 7, 7, 6, "oui", "oui", "oui", 3),
            actif("NSBC", "Nsia Banque CI", "CI", "Banque", 6100.0, 5800.0, 5200.0, 7500.0, 6, 7, 6, 7, 6, 7, 5, "oui", "moyen", "oui", 2),
            actif("ONTBF", "Onatel BF", "BF", "Telecom", 4800.0, 4500.0, 4000.0, 5800.0, 6, 7, 6, 7, 7, 7, 5, "oui", "faible", "oui", 2),
            actif("BOAB", "BOA Bénin", "BJ", "Banque", 5800.0, 5500.0, 5000.0, 7000.0, 6, 6, 5, 6, 5, 7, 5, "oui", "moyen", "non", 2),
            actif("PALC", "Palm CI", "CI", "Agriculture", 7600.0, 7200.0, 6500.0, 9000.0, 6, 6, 6, 6, 7, 6, 4, "oui", "faible", "non", 1),
            actif("NTLC", "NESTLE CI", "CI", "Industrie", 14850.0, 14000.0, 12500.0, 17500.0, 7, 8, 7, 8, 7, 8, 6, "oui", "moyen", "oui", 2),
            actif("ORAC", "ORANGE CI", "CI", "Telecom", 15850.0, 15000.0, 13500.0, 18500.0, 8, 8, 7, 8, 8, 8, 7, "oui", "moyen", "oui", 2),
            actif("UNLC", "UNILEVER CI", "CI", "Distribution", 55800.0, 53000.0, 48000.0, 65000.0, 7, 8, 7, 8, 8, 8, 6, "oui", "moyen", "oui", 2),
            actif("SMBC", "SMB CI", "CI", "Industrie", 12000.0, 11500.0, 10500.0, 14000.0, 4, 5, 4, 5, 4, 5, 3, "non", "faible", "non", 1),
            actif("ECOC", "Ecobank CI", "CI", "Banque", 6500.0, 6200.0, 5600.0, 7800.0, 5, 5, 4, 5, 4, 6, 3, "non", "faible", "non", 1),
            actif("BOAS", "BOA Sénégal", "SN", "Banque", 3600.0, 3400.0, 3000.0, 4400.0, 3, 4, 3, 4, 3, 5, 2, "non", "non", "non", 1),
            actif("TTLC", "Total CI", "CI", "Energie", 2100.0, 2000.0, 1800.0, 2600.0, 5, 5, 4, 5, 4, 6, 3, "non", "moyen", "non", 1),
            actif("SIBC", "SIB CI", "CI", "Banque", 5200.0, 5000.0, 4500.0, 6200.0, 5, 4, 3, 4, 3, 5, 3, "non", "faible", "non", 1),
            actif("BOABF", "BOA Burkina", "BF", "Banque", 3900.0, 3700.0, 3300.0, 4700.0, 3, 3, 3, 3, 3, 4, 2, "non", "non", "non", 1),
            actif("ETIT", "Ecobank Togo", "TG", "Banque", 22.0, 20.0, 18.0, 28.0, 3, 3, 2, 3, 2, 4, 2, "non", "non", "non", 1),
            actif("BOAN", "BOA Niger", "NE", "Banque", 4100.0, 3900.0, 3500.0, 5000.0, 2, 3, 2, 3, 2, 3, 1, "non", "non", "non", 1),
            actif("ORGT", "Oragroup Togo", "TG", "Banque", 5400.0, 5200.0, 4700.0, 6500.0, 2, 3, 2, 3, 2, 3, 1, "non", "non", "non", 1),
            actif("STAC", "SETAO CI", "CI", "Transport", 1200.0, 1100.0, 950.0, 1500.0, 2, 2, 2, 2, 2, 3, 1, "non", "non", "non", 1),
            actif("NEIC", "NEI CEDA CI", "CI", "Distribution", 780.0, 750.0, 650.0, 950.0, 2, 2, 2, 2, 2, 3, 1, "non", "non", "non", 1),
            actif("ABJC", "Servair Abidjan CI", "CI", "Transport", 3570.0, 3400.0, 3000.0, 4200.0, 5, 5, 4, 5, 4, 6, 3, "non", "faible", "non", 1),
            actif("BNBC", "Banque Nationale BCI", "CI", "Banque", 1900.0, 1800.0, 1600.0, 2300.0, 3, 3, 3, 3, 3, 4, 2, "non", "non", "non", 1),
            actif("BOAC", "BOA Côte d'Ivoire", "CI", "Banque", 8970.0, 8500.0, 7800.0, 10500.0, 6, 6, 5, 6, 5, 6, 4, "non", "moyen", "non", 2),
            actif("BOAM", "BOA Mali", "ML", "Banque", 4435.0, 4200.0, 3800.0, 5300.0, 3, 3, 3, 3, 3, 4, 2, "non", "non", "non", 1),
            actif("CABC", "Crédit Agricole BCI", "CI", "Banque", 3900.0, 3700.0, 3300.0, 4700.0, 5, 5, 4, 5, 4, 6, 3, "non", "faible", "non", 1),
            actif("CFAC", "CFAO CI", "CI", "Distribution", 1645.0, 1600.0, 1400.0, 2000.0, 5, 5, 4, 5, 4, 6, 3, "non", "moyen", "non", 1),
            actif("CIEC", "COTE D'IVOIRE ENERGIE", "CI", "Energie", 4995.0, 4800.0, 4300.0, 6000.0, 5, 6, 5, 6, 5, 6, 4, "non", "moyen", "non", 2),
            actif("FTSC", "FILTISAC CI", "CI", "Industrie", 2190.0, 2100.0, 1900.0, 2600.0, 3, 3, 2, 3, 2, 4, 2, "non", "non", "non", 1),
            actif("LNBB", "La Nationale des Banques BJ", "BJ", "Banque", 3900.0, 3700.0, 3300.0, 4700.0, 3, 3, 2, 3, 2, 4, 2, "non", "non", "non", 1),
            actif("PRSC", "TRACTAFRIC MOTORS CI", "CI", "Transport", 4105.0, 3900.0, 3500.0, 4900.0, 3, 4, 3, 4, 3, 4, 2, "non", "faible", "non", 1),
            actif("SAFC", "SAFCA CI", "CI", "Distribution", 3945.0, 3800.0, 3400.0, 4700.0, 3, 3, 2, 3, 2, 4, 2, "non", "non", "non", 1),
            actif("SCRC", "SUCRIVOIRE CI", "CI", "Agriculture", 3100.0, 3000.0, 2700.0, 3700.0, 3, 4, 3, 4, 3, 4, 2, "non", "faible", "non", 1),
            actif("SDCC", "SODECI CI", "CI", "Energie", 12005.0, 11500.0, 10500.0, 14000.0, 5, 6, 5, 6, 5, 6, 4, "non", "moyen", "non", 2),
            actif("SDSC", "SOLIBRA CI", "CI", "Industrie", 2280.0, 2200.0, 2000.0, 2700.0, 3, 3, 2, 3, 2, 4, 2, "non", "non", "non", 1),
            actif("SEMC", "CROWN SIEM CI", "CI", "Industrie", 1590.0, 1500.0, 1300.0, 1900.0, 2, 2, 2, 2, 2, 3, 1, "non", "non", "non", 1),
            actif("SHEC", "SHELL CI", "CI", "Energie", 2040.0, 1900.0, 1700.0, 2400.0, 5, 5, 4, 5, 4, 5, 3, "non", "faible", "non", 1),
            actif("SICC", "SICOR CI", "CI", "Agriculture", 4460.0, 4200.0, 3800.0, 5200.0, 3, 4, 3, 4, 3, 4, 2, "non", "faible", "non", 1),
            actif("SIVC", "AIR LIQUIDE CI", "CI", "Industrie", 2795.0, 2700.0, 2400.0, 3300.0, 3, 3, 2, 3, 2, 4, 2, "non", "non", "non", 1),
            actif("SOGC", "SOGECI CI", "CI", "Distribution", 8395.0, 8000.0, 7200.0, 9800.0, 3, 4, 3, 4, 3, 4, 2, "non", "faible", "non", 1),
            actif("SPHC", "SAPH CI", "CI", "Agriculture", 7590.0, 7200.0, 6500.0, 8900.0, 5, 6, 5, 6, 5, 6, 4, "non", "moyen", "non", 2),
            actif("STBC", "SETAO BCI", "CI", "Transport", 22095.0, 21000.0, 19000.0, 26000.0, 3, 3, 2, 3, 2, 4, 2, "non", "non", "non", 1),
            actif("SVOC", "SIVOA CI", "CI", "Agriculture", 950.0, 900.0, 800.0, 1150.0, 2, 2, 2, 2, 2, 3, 1, "non", "non", "non", 1),
            actif("TTLS", "TOTAL SN", "SN", "Energie", 3395.0, 3200.0, 2900.0, 4000.0, 3, 4, 3, 4, 3, 4, 2, "non", "faible", "non", 1),
            actif("UNXC", "UNIWAX CI", "CI", "Industrie", 1970.0, 1900.0, 1700.0, 2300.0, 3, 4, 3, 4, 3, 4, 2, "non", "faible", "non", 1)
        );
    }

    private Actif actif(String ticker, String nom, String pays, String secteur,
                        Double prix, Double entree, Double sl, Double tp,
                        Integer liq, Integer ca, Integer croiss, Integer marge,
                        Integer per5, Integer freq, Integer notediv,
                        String sigM, String sigW, String sigD, Integer dailyM) {
        Actif a = new Actif();
        a.setTicker(ticker);
        a.setNom(nom);
        a.setPays(pays);
        a.setSecteur(secteur);
        a.setPrix(prix);
        a.setEntree(entree);
        a.setSl(sl);
        a.setTp(tp);
        a.setSigM(sigM);
        a.setSigW(sigW);
        a.setSigD(sigD);
        a.setDailyM(dailyM);

        // Nouvelle formule D.W
        a.setLiq(liq);
        a.setCa(ca);
        a.setCroiss(croiss);
        a.setMarge(marge);
        a.setPer5(per5.doubleValue());
        a.setFreq(freq.doubleValue());
        a.setNotediv(notediv.doubleValue());

        // NOTE = ((LIQ+CA+CROISS+MARGE+PER5+FREQ)/6) × 10
        double note = ((liq + ca + croiss + marge + per5 + freq) / 6.0) * 10.0;
        double global = (note * 0.7) + (notediv * 0.3);

        a.setNote((int) Math.round(note));
        a.setScore((int) Math.round(global));

        // Sizing selon seuils D.W
        int sizing;
        if (global > 80) sizing = 4;
        else if (global >= 60) sizing = 3;
        else if (global >= 40) sizing = 2;
        else sizing = 1;
        a.setSizing(sizing);

        return a;
    }
}