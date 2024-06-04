package org.galvanica.service.operazioniBagno;

import org.galvanica.model.StoricoDettaglio;
import org.galvanica.model.StoricoGenerale;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StoriciAnnullaOConcludiService {
    private final BagnoRepository bagnoRepository;
    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final StoricoDettaglioRepository storicoDettaglioRepository;


    //todo: impostare i controlli a monte di tutti i metodi

    public StoriciAnnullaOConcludiService(BagnoRepository bagnoRepository,
                                          StoricoGeneraleRepository storicoGeneraleRepository,
                                          StoricoDettaglioRepository storicoDettaglioRepository,
                                          AlimentazioneRepository alimentazioneRepository) {
        this.bagnoRepository = bagnoRepository;
        this.storicoGeneraleRepository = storicoGeneraleRepository;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
    }

    public void confermaInteraAlimentazione(Long idStoricoGenerale) {
        StoricoGenerale storicoGenerale = trovaStoricoGenerale(idStoricoGenerale);
        if (!storicoGenerale.getStoricoDettaglioList().isEmpty()) {
            for (StoricoDettaglio storicoDettaglio : storicoGenerale.getStoricoDettaglioList()) {
                eseguiStoricoDettaglio(storicoDettaglio);
            }
        }
        concludiStoricoGenerale(storicoGenerale);
    }

    public void eseguiSingolaAggiunta(Long idStoricoDettaglio) {
        StoricoDettaglio storicoDettaglio = trovaStoricoDettaglio(idStoricoDettaglio);
        if (storicoDettaglio.getEscluso()) {
            throw new RuntimeException(
                    "se il DettaglioStorico è escluso non può essere anche eseguito");
        }
        eseguiStoricoDettaglio(storicoDettaglio);
        if (listaStoricoDettaglioNONCompletata(storicoDettaglio.getStoricoGenerale())) {
            return;
            //todo: non so se conviene fare uscire qualcosa dal metodo per capire se anche lo storicoGenerale è aggiornato oppure no.
            //aggiornare gli scatti solo se l-aggiunta che sto confermando e la piu vecchia aggiunta da eseguire nel bagno
        }
        concludiStoricoGenerale(storicoDettaglio.getStoricoGenerale());
    }

    //metodi per confermaInteraAlimentazione e eseguiSingolaAggiunta.
    private void eseguiStoricoDettaglio(StoricoDettaglio storicoDettaglio) {
        if (storicoDettaglio.getEscluso()) {
            return;
        }
        storicoDettaglio.setEseguito(true);
        storicoDettaglioRepository.save(storicoDettaglio);
    }

    private void concludiStoricoGenerale(StoricoGenerale storicoGenerale) {
        if (storicoGenerale.getConcluso()) {
            throw new RuntimeException("lo storico generale è già concluso");
        }
        if (listaStoricoDettaglioNONCompletata(storicoGenerale)) {
            throw new RuntimeException(
                    "non tutti i Dettagli storico sono eseguiti o esclusi");
        }
        storicoGenerale.setConcluso(true);
        storicoGenerale.setDataFine(LocalDateTime.now());
        storicoGeneraleRepository.save(storicoGenerale);
    }


    //metodi per tutto
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

    private boolean listaStoricoDettaglioNONCompletata(
            StoricoGenerale storicoGenerale) {
        return storicoGenerale.getStoricoDettaglioList()
                .stream()
                .anyMatch(storicoDettaglio -> !storicoDettaglio.getEseguito() && !storicoDettaglio.getEscluso());
    }


    private void annullaStoricoGenerale(StoricoGenerale storicoGenerale) {
        if (storicoGenerale.getConcluso()) {
            throw new RuntimeException("lo storico generale è già concluso");
        }
        storicoGenerale.setConcluso(true);
        storicoGenerale.setAnnullato(true);
        storicoGenerale.setDataFine(LocalDateTime.now());
        storicoGeneraleRepository.save(storicoGenerale);
    }

    public void escludiSingolaAggiunta(Long idStoricoDettaglio) {
        StoricoDettaglio storicoDettaglio = trovaStoricoDettaglio(idStoricoDettaglio);
        if (storicoDettaglio.getEseguito()) {
            throw new RuntimeException(
                    "se il DettaglioStorico è eseguito, non può essere anche escluso");
        }
        escludiStoricoDettaglio(storicoDettaglio);
        if (listaStoricoDettaglioNONCompletata(storicoDettaglio.getStoricoGenerale())) {
            return;
            //todo: non so se conviene fare uscire qualcosa dal metodo per capire se anche lo storicoGenerale è aggiornato oppure no.
            //aggiornare gli scatti solo se l-aggiunta che sto confermando e la piu vecchia aggiunta da eseguire nel bagno
        }
        aggiornaStoricoGenerale(storicoDettaglio.getStoricoGenerale());
    }

    private void escludiStoricoDettaglio(StoricoDettaglio storicoDettaglio) {
        if (storicoDettaglio.getEseguito()) {
            return;
        }
        storicoDettaglio.setEscluso(true);
        storicoDettaglioRepository.save(storicoDettaglio);
    }

    private void aggiornaStoricoGenerale(
            StoricoGenerale storicoGenerale) {
        List<StoricoDettaglio> storicoDettaglioList = storicoGenerale.getStoricoDettaglioList();
        long totConclusi = storicoDettaglioList.stream()
                .filter(dettaglio -> dettaglio.getEseguito() || dettaglio.getEscluso())
                .count();
        long totEsclusi = storicoDettaglioList.stream()
                .filter(StoricoDettaglio::getEscluso)
                .count();
        if (totEsclusi == storicoDettaglioList.size()) {
            annullaStoricoGenerale(storicoGenerale);
            return;
        }
        if (totConclusi == storicoDettaglioList.size()) {
            concludiStoricoGenerale(storicoGenerale);
        }
    }

    public void annullaInteraAlimentazione(Long idStoricoGenerale) {
        StoricoGenerale storicoGenerale = trovaStoricoGenerale(idStoricoGenerale);
        if (!storicoGenerale.getStoricoDettaglioList().isEmpty()) {
            for (StoricoDettaglio storicoDettaglio : storicoGenerale.getStoricoDettaglioList()) {
                if (storicoDettaglio.getEseguito()) {
                    throw new RuntimeException("vi è almeno un aggiunta eseguita. " +
                            "Per annullare l'intera alimentazione nessuna aggiunta deve essere eseguita.");
                }
                if (!storicoDettaglio.getEscluso()) {
                    escludiStoricoDettaglio(storicoDettaglio);
                }
            }
        }
        annullaStoricoGenerale(storicoGenerale);
    }

    public void eseguiSingolaAggiuntaList(List<Long> idStoricoDettaglioList) {
        idStoricoDettaglioList = storicoDettaglioRepository.orderAscIdList(
                idStoricoDettaglioList);
        for (Long idStoricoDettaglio : idStoricoDettaglioList) {
            eseguiSingolaAggiunta(idStoricoDettaglio);
        }
    }

    public void escludiSingolaAggiuntaList(List<Long> idStoricoDettaglioList) {
        for (Long idStoricoDettaglio : idStoricoDettaglioList) {
            escludiSingolaAggiunta(idStoricoDettaglio);
        }
    }
}
