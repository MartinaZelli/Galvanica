package org.galvanica.service;

import org.galvanica.dto.AlimentazioneScattiRisposta;
import org.galvanica.math.MetodiArrotondamenti;
import org.galvanica.math.ScattiMath;
import org.galvanica.model.*;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OperazioniBagnoService {

    private final BagnoRepository bagnoRepository;
    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final StoricoDettaglioRepository storicoDettaglioRepository;


    public OperazioniBagnoService(BagnoRepository bagnoRepository,
                                  StoricoGeneraleRepository storicoGeneraleRepository,
                                  StoricoDettaglioRepository storicoDettaglioRepository) {
        this.bagnoRepository = bagnoRepository;
        this.storicoGeneraleRepository = storicoGeneraleRepository;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
    }

    public void eseguiSingolaAggiunta(Long idStoricoDettaglio) {
        StoricoDettaglio storicoDettaglio = trovaStoricoDettaglio(idStoricoDettaglio);
        if (storicoDettaglio.getEscluso()) {
            throw new RuntimeException(
                    "se il DettaglioStorico è escluso non può essere anche eseguito");
        }
        eseguiStoricoDettaglio(storicoDettaglio);
        if (!veroSeListaStoricoDettaglioCompletata(storicoDettaglio.getStoricoGenerale())) {
            return;
            //todo: non so se conviene fare uscire qualcosa dal metodo per capire se anche lo storicoGenerale è aggiornato oppure no.
        }
        aggiornaBagnoEStoricoGenerale(storicoDettaglio.getStoricoGenerale());
    }

    public void confermaInteraAlimentazione(Long idStoricoGenerale) {
        StoricoGenerale storicoGenerale = trovaStoricoGenerale(idStoricoGenerale);
        if (!storicoGenerale.getStoricoDettaglioList().isEmpty()) {
            for (StoricoDettaglio storicoDettaglio : storicoGenerale.getStoricoDettaglioList()) {
                eseguiStoricoDettaglio(storicoDettaglio);
            }
        }
        aggiornaBagnoEStoricoGenerale(storicoGenerale);
    }

    public AlimentazioneScattiRisposta calcolaAlimentazioneScatti(Long id,
                                                                  Integer scattiParziali) {
        Bagno bagno = trovaBagno(id);
        Alimentazione alimentazione = trovaAlimentazioneScatti(bagno);

        int scattiAttuali = bagno.getRestoScatti() + scattiParziali;

        Double primoValoreVolumetrico = trovaPrimoValoreVolumetrico(alimentazione);

        ScattiMath scattiMath = MetodiArrotondamenti.alimentazioneScattiMath(
                scattiAttuali,
                alimentazione.getScatti(),
                alimentazione.getArrotondaValori(),
                primoValoreVolumetrico);

        if (scattiMath.getMoltiplicatoreAlimentazione() == 0) {
            System.out.println(
                    "il bagno non richiede aggiunte per ora. nuovo resto scatti : " + scattiAttuali);
            bagno.setScattiTotali(bagno.getScattiTotali() + scattiParziali);
            bagno.setRestoScatti(scattiAttuali);
            bagnoRepository.save(bagno);
            return AlimentazioneScattiRisposta.builder()
                    .idBagno(id)
                    .restoScatti(scattiAttuali)
                    .messaggio("gli scatti sono inferiori al 90% dell'alimentazione,"
                            + "le aggiunte non verranno eseguite ma messe in conto per la prossima chiamata.")
                    .moltiplicatoreAlimentazione(0D)
                    .build();
        }
        StoricoGenerale storicoGenerale = storicoGeneraleRepository.save(
                StoricoGenerale.builder()
                        .alimentazione(alimentazione)
                        .bagno(bagno)
                        .scattiTotali(bagno.getScattiTotali() + scattiParziali)
                        .restoScatti((int) scattiMath.getRestoScatti())
                        .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                        .build());
        Map<String, String> mappaAggiunta = conteggiProdottiScatti(alimentazione,
                scattiMath,
                storicoGenerale);

        return AlimentazioneScattiRisposta.builder()
                .idBagno(id)
                .mappaAlimentazione(mappaAggiunta)
                .restoScatti((int) scattiMath.getRestoScatti())
                .moltiplicatoreAlimentazione(scattiMath.getMoltiplicatoreAlimentazione())
                .build();
    }


    private Map<String, String> conteggiProdottiScatti(Alimentazione alimentazione,
                                                       ScattiMath scattiMath,
                                                       StoricoGenerale storicoGenerale) {
        Map<String, String> mappaAggiunta = new HashMap<>();
        StoricoGenerale storico = trovaStoricoGenerale(storicoGenerale.getIdStorico());
        for (DettaglioAlimentazione dettaglio : alimentazione.getDettaglioAlimentazioneList()) {
            double quantitaProdottoAggiunta = dettaglio.getQuantitaProdotto() * scattiMath.getMoltiplicatoreAlimentazione();
            if (dettaglio.getUnitaDiMisura().isSonoVolume()) {
                quantitaProdottoAggiunta = MetodiArrotondamenti.approssimazioneAggiunta(
                        quantitaProdottoAggiunta);
            }
            storicoDettaglioRepository.save(StoricoDettaglio.builder()
                    .prodotto(dettaglio.getProdotto())
                    .storicoGenerale(storico)
                    .quantita(quantitaProdottoAggiunta)
                    .unitaDiMisura(dettaglio.getUnitaDiMisura())
                    .build());

            mappaAggiunta.put(dettaglio.getProdotto().getNome(),
                    quantitaProdottoAggiunta + " " + dettaglio.getUnitaDiMisura()
                            .name());
        }
        return mappaAggiunta;
    }

    private void aggiornaBagnoEStoricoGenerale(StoricoGenerale storicoGenerale) {
        storicoGenerale.setConcluso(true);
        storicoGenerale.setDataFine(LocalDateTime.now());
        storicoGeneraleRepository.save(storicoGenerale);
        storicoGenerale.getBagno().setRestoScatti(storicoGenerale.getRestoScatti());
        storicoGenerale.getBagno()
                .setScattiTotali(storicoGenerale.getScattiTotali());
        bagnoRepository.save(storicoGenerale.getBagno());
    }

    private boolean veroSeListaStoricoDettaglioCompletata(
            StoricoGenerale storicoGenerale) {
        return storicoGenerale.getStoricoDettaglioList()
                .stream()
                .noneMatch(storicoDettaglio -> !storicoDettaglio.getEseguito() && !storicoDettaglio.getEscluso());
    }

    private void eseguiStoricoDettaglio(StoricoDettaglio storicoDettaglio) {
        if (storicoDettaglio.getEscluso()) {
            return;
        }
        storicoDettaglio.setEseguito(true);
        storicoDettaglioRepository.save(storicoDettaglio);
    }

    //todo: tutti i metodi trova"oggetto" possono essere semplificati?
    private StoricoDettaglio trovaStoricoDettaglio(Long id) {
        Optional<StoricoDettaglio> storicoDettaglioTrovato = storicoDettaglioRepository.findById(
                id);
        return storicoDettaglioTrovato.orElseThrow(() -> new RuntimeException(
                "storico non trovato per id " + id));
    }

    private StoricoGenerale trovaStoricoGenerale(long id) {
        Optional<StoricoGenerale> storicoGeneraleTrovato = storicoGeneraleRepository.findById(
                id);
        return storicoGeneraleTrovato.orElseThrow(() -> new RuntimeException(
                "storico non trovato per id " + id));
    }

    private Bagno trovaBagno(long id) {
        Optional<Bagno> bagnoTrovato = bagnoRepository.findById(id);
        return bagnoTrovato.orElseThrow(() -> new RuntimeException(
                "bagno non trovato per id " + id));
    }

    private Alimentazione trovaAlimentazioneScatti(Bagno bagno) {
        return bagno.getAlimentazioneList()
                .stream()
                .filter(alimentazioneFilter -> alimentazioneFilter.getScatti() != null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Alimentazione a scatti non trovata per bagno " + bagno.getIdBagno() + " nome: " + bagno.getNome()));
    }

    private Double trovaPrimoValoreVolumetrico(Alimentazione alimentazione) {
        return alimentazione
                .getDettaglioAlimentazioneList()
                .stream()
                .filter(dettaglioAlimentazione -> dettaglioAlimentazione.getUnitaDiMisura()
                        .isSonoVolume())
                .findFirst()
                .map(DettaglioAlimentazione::getQuantitaProdotto)
                .orElse(null);
    }
}
