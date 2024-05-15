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
import java.util.Objects;
import java.util.Optional;

@Service
public class OperazioniInStorico {
    private final BagnoRepository bagnoRepository;
    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final StoricoDettaglioRepository storicoDettaglioRepository;


    public OperazioniInStorico(BagnoRepository bagnoRepository,
                               StoricoGeneraleRepository storicoGeneraleRepository,
                               StoricoDettaglioRepository storicoDettaglioRepository,
                               AlimentazioneRepository alimentazioneRepository) {
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
            //aggiornare gli scatti solo se l-aggiunta che sto confermando e la piu vecchia aggiunta da eseguire nel bagno
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

    private void aggiornaBagnoEStoricoGenerale(
            StoricoGenerale storicoGenerale) {
        Boolean sonoScatti = storicoGenerale.getSonoScatti();
        if (sonoScatti == null) {
            throw new RuntimeException(
                    "il valore booleano 'sonoScatti' di questo storicoGenerale è null");
        }
        List<StoricoGenerale> storicoGeneraleListDaEseguire = storicoGeneraleRepository
                .storicoGeneraleDescList(false,
                        storicoGenerale.getBagno().getIdBagno(),
                        sonoScatti);
        if (!Objects.equals(storicoGeneraleListDaEseguire.getLast()
                .getIdStorico(), storicoGenerale.getIdStorico())) {
            throw new RuntimeException(
                    "vanno aggiornati gli storici dal più vecchio al più nuovo");
        }
        if (sonoScatti) {
            storicoGenerale.getBagno()
                    .setRestoScatti(storicoGenerale.getRestoScatti());
            storicoGenerale.getBagno()
                    .setScattiTotali(storicoGenerale.getScattiTotali());
            bagnoRepository.save(storicoGenerale.getBagno());
        }
        storicoGenerale.setConcluso(true);
        storicoGenerale.setDataFine(LocalDateTime.now());
        storicoGeneraleRepository.save(storicoGenerale);
    }

    private void eseguiStoricoDettaglio(StoricoDettaglio storicoDettaglio) {
        if (storicoDettaglio.getEscluso()) {
            return;
        }
        storicoDettaglio.setEseguito(true);
        storicoDettaglioRepository.save(storicoDettaglio);
    }

    private StoricoGenerale trovaStoricoGenerale(long id) {
        Optional<StoricoGenerale> storicoGeneraleTrovato = storicoGeneraleRepository.findById(
                id);
        return storicoGeneraleTrovato.orElseThrow(() -> new RuntimeException(
                "storico non trovato per id " + id));
    }

    private boolean veroSeListaStoricoDettaglioCompletata(
            StoricoGenerale storicoGenerale) {
        return storicoGenerale.getStoricoDettaglioList()
                .stream()
                .noneMatch(storicoDettaglio -> !storicoDettaglio.getEseguito() && !storicoDettaglio.getEscluso());
    }

    private StoricoDettaglio trovaStoricoDettaglio(Long id) {
        Optional<StoricoDettaglio> storicoDettaglioTrovato = storicoDettaglioRepository.findById(
                id);
        return storicoDettaglioTrovato.orElseThrow(() -> new RuntimeException(
                "storico non trovato per id " + id));
    }

    public void escludiSingolaAggiunta(Long idStoricoDettaglio) {
        StoricoDettaglio storicoDettaglio = trovaStoricoDettaglio(idStoricoDettaglio);
        if (storicoDettaglio.getEseguito()) {
            throw new RuntimeException(
                    "se il DettaglioStorico è eseguito, non può essere anche escluso");
        }
        escludiStoricoDettaglio(storicoDettaglio);
        if (!veroSeListaStoricoDettaglioCompletata(storicoDettaglio.getStoricoGenerale())) {
            return;
            //todo: non so se conviene fare uscire qualcosa dal metodo per capire se anche lo storicoGenerale è aggiornato oppure no.
            //aggiornare gli scatti solo se l-aggiunta che sto confermando e la piu vecchia aggiunta da eseguire nel bagno
        }
        if (!aggiornaStoricoGeneraleDaAnnullare(storicoDettaglio.getStoricoGenerale())) {
            aggiornaBagnoEStoricoGenerale(storicoDettaglio.getStoricoGenerale());
        }
    }

    private void escludiStoricoDettaglio(StoricoDettaglio storicoDettaglio) {
        if (storicoDettaglio.getEseguito()) {
            return;
        }
        storicoDettaglio.setEscluso(true);
        storicoDettaglioRepository.save(storicoDettaglio);
    }

    private boolean aggiornaStoricoGeneraleDaAnnullare(
            StoricoGenerale storicoGenerale) {
        List<StoricoDettaglio> storicoDettaglioList = storicoGenerale.getStoricoDettaglioList();
        for (StoricoDettaglio dettaglio : storicoDettaglioList) {
            if (dettaglio.getEseguito() || !dettaglio.getEscluso()) {
                return false;
            }
        }
        storicoGenerale.setAnnullato(true);
        storicoGenerale.setConcluso(true);
        storicoGenerale.setDataFine(LocalDateTime.now());
        storicoGeneraleRepository.save(storicoGenerale);
        return true;
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
        storicoGenerale.setAnnullato(true);
        storicoGenerale.setConcluso(true);
        storicoGenerale.setDataFine(LocalDateTime.now());
        storicoGeneraleRepository.save(storicoGenerale);
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
