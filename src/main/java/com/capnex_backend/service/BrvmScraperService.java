package com.capnex_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.capnex_backend.entity.Actif;
import com.capnex_backend.entity.PrixHistorique;
import com.capnex_backend.repository.ActifRepository;
import com.capnex_backend.repository.PrixHistoriqueRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrvmScraperService {

    private final ActifRepository actifRepository;
    private final ActifService actifService;
    private final WebSocketService webSocketService;
    private final PrixHistoriqueRepository prixHistoriqueRepository;
    private final AlerteService alerteService;

    // Heures de cotation BRVM : 9h00 - 15h30 (heure Abidjan = UTC+0)
    private static final LocalTime OUVERTURE = LocalTime.of(9, 0);
    private static final LocalTime FERMETURE = LocalTime.of(15, 30);

    // Toutes les 15 minutes
    @Scheduled(fixedRate = 900000)
    public void scraperPrix() {
        LocalTime now = LocalTime.now();

        // Scrape uniquement pendant les heures de cotation
        if (now.isBefore(OUVERTURE) || now.isAfter(FERMETURE)) {
            log.info("BRVM fermée — pas de scraping ({})", now);
            return;
        }

        scraperMaintenant();

        try {
            Map<String, Double> prix = scrapeDepuisBrvm();
            if (prix.isEmpty()) {
                log.warn("Aucun prix récupéré depuis brvm.org");
                return;
            }

            int updated = 0;
            for (Map.Entry<String, Double> entry : prix.entrySet()) {
                String ticker = entry.getKey()
                        .trim()
                        .toUpperCase()
                        .replace(" ", "");
                Double prixActuel = entry.getValue();

                actifRepository.findByTicker(ticker).ifPresent(actif -> {
                    actif.setPrix(prixActuel);
                    // Recalcul score et sizing
                    int score = actifService.calculerScore(actif);
                    actif.setScore(score);
                    actif.setSizing(actifService.calculerSizing(score, actif.getSigM(), actif.getLiq()));
                    actifRepository.save(actif);
                });
                updated++;
            }

            log.info("✅ {} actifs mis à jour", updated);

        } catch (Exception e) {
            log.error("Erreur scraping BRVM : {}", e.getMessage());
        }
    }

    private Map<String, Double> scrapeDepuisBrvm() throws Exception {
    Map<String, Double> prix = new HashMap<>();

    // Désactiver SSL pour brvm.org
    javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
        new javax.net.ssl.X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
        }
    };

    javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

    Document doc = Jsoup.connect("https://www.brvm.org/fr/cours-actions/0/")
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0.0.0 Safari/537.36")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .header("Accept-Language", "fr-FR,fr;q=0.9")
            .sslSocketFactory(sc.getSocketFactory())
            .timeout(20000)
            .ignoreHttpErrors(true)
            .get();

    log.info("Page récupérée : {}", doc.title());

    // Essai sélecteurs
    Elements rows = doc.select("table#tablecours tbody tr");
    log.info("Sélecteur 1 (table#tablecours) : {} lignes", rows.size());

    if (rows.isEmpty()) {
        rows = doc.select("table tbody tr");
        log.info("Sélecteur 2 (table tbody tr) : {} lignes", rows.size());
    }

    if (rows.isEmpty()) {
        rows = doc.select("tr.odd, tr.even");
        log.info("Sélecteur 3 (tr.odd/even) : {} lignes", rows.size());
    }

    if (rows.isEmpty()) {
        log.warn("Aucune ligne. HTML partiel : {}",
            doc.body().html().substring(0, Math.min(2000, doc.body().html().length())));
        return prix;
    }

    for (Element row : rows) {
    Elements cols = row.select("td");
    if (cols.size() < 6) continue;

    try {
        // Colonne 0 = ticker
        String ticker = cols.get(0).text().trim().toUpperCase().replace(" ", "");

        // Colonne 5 = cours actuel (clôture)
        String prixStr = cols.get(5).text().trim()
                .replace(" ", "")
                .replace("\u00a0", "")
                .replace(",", ".")
                .replace("'", "");

        double prixVal = Double.parseDouble(prixStr);

        // Filtrer les lignes parasites (capitalisation, indices...)
        if (ticker.length() >= 3 && ticker.length() <= 6 && prixVal > 0) {
            prix.put(ticker, prixVal);
        }

    } catch (NumberFormatException e) {
        // Ligne non parseable, on continue
    }
}
    log.info("Prix extraits ({}) : {}", prix.size(), prix);
    return prix;
}
    // Méthode manuelle pour forcer un scraping immédiat
     public Map<String, Double> scraperMaintenant() {
        log.info("Scraping manuel déclenché");
        try {
            Map<String, Double> prix = scrapeDepuisBrvm();

            int updated = 0;
            for (Map.Entry<String, Double> entry : prix.entrySet()) {
                String ticker = entry.getKey().trim().toUpperCase();
                Double prixActuel = entry.getValue();

                var actifOpt = actifRepository.findByTicker(ticker);
                if (actifOpt.isPresent()) {
                    Actif actif = actifOpt.get();
                    int ancienSizing = actif.getSizing() != null ? actif.getSizing() : -1;
                    actif.setPrix(prixActuel);
                    int score = actifService.calculerScore(actif);
                    actif.setScore(score);
                    int nouveauSizing = actifService.calculerSizing(score, actif.getSigM(), actif.getLiq());
                    actif.setSizing(nouveauSizing);
                    actifRepository.save(actif);

                    sauvegarderHistorique(ticker, prixActuel);

                    // Alerte si sizing change vers 0X
                    if (ancienSizing != nouveauSizing) {
                        alerteService.creerAlerteChangementSignal(ticker, ancienSizing, nouveauSizing);
                    }
                    // Alerte Stop Loss
                    if (actif.getSl() != null && actif.getSl() > 0 && prixActuel <= actif.getSl()) {
                        alerteService.creerAlerteStopLoss(ticker, prixActuel, actif.getSl());
                    }
                    updated++;
                }
            }

            // Push tous les prix via WebSocket
            webSocketService.envoyerPrix(prix);

            log.info("✅ {}/{} actifs mis à jour", updated, prix.size());
            return prix;

        } catch (Exception e) {
            log.error("Erreur scraping manuel : {}", e.getMessage());
            return new HashMap<>();
        }
    }

    public List<Map<String, Object>> scraperHistorique(String ticker, int jours) {
        List<Map<String, Object>> historique = new ArrayList<>();

        try {
            // URL historique BRVM
            String url = "https://www.brvm.org/fr/cours-actions/0/" + ticker.toLowerCase();

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .sslSocketFactory(getSSLSocketFactory())
                    .timeout(20000)
                    .ignoreHttpErrors(true)
                    .get();

            log.info("Page historique {} : {}", ticker, doc.title());

            // Cherche le tableau historique
            Elements rows = doc.select("table tbody tr");
            log.info("Lignes trouvées : {}", rows.size());

            for (Element row : rows) {
                Elements cols = row.select("td");
                if (cols.size() < 2) continue;

                try {
                    String date = cols.get(0).text().trim();
                    String prixStr = cols.get(1).text().trim()
                            .replace(" ", "")
                            .replace("\u00a0", "")
                            .replace(",", ".");

                    double prix = Double.parseDouble(prixStr);

                    Map<String, Object> point = new HashMap<>();
                    point.put("date", date);
                    point.put("prix", prix);
                    historique.add(point);

                } catch (NumberFormatException e) {
                    // Continue
                }
            }

            log.info("✅ {} points historiques pour {}", historique.size(), ticker);

        } catch (Exception e) {
            log.error("Erreur historique {} : {}", ticker, e.getMessage());
        }

        return historique;
    }

    private javax.net.ssl.SSLSocketFactory getSSLSocketFactory() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{
            new javax.net.ssl.X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(java.security.cert.X509Certificate[] c, String a) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] c, String a) {}
            }
        };
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        return sc.getSocketFactory();
    }

    private void sauvegarderHistorique(String ticker, Double prix) {
        LocalDate today = LocalDate.now();
        
        // Un seul enregistrement par jour par ticker
        if (!prixHistoriqueRepository.existsByTickerAndDateCotation(ticker, today)) {
            PrixHistorique ph = new PrixHistorique();
            ph.setTicker(ticker);
            ph.setPrix(prix);
            ph.setDateCotation(today);
            prixHistoriqueRepository.save(ph);
            log.info("📈 Historique sauvegardé : {} = {} XOF ({})", ticker, prix, today);
        }
    }
}
