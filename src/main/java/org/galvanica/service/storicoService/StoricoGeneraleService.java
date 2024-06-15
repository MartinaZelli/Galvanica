package org.galvanica.service.storicoService;

import org.galvanica.dto.dtoConModel.StoricoGeneraleDto;
import org.galvanica.model.StoricoDettaglio;
import org.galvanica.model.StoricoGenerale;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StoricoGeneraleService {
    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final StoricoDettaglioRepository storicoDettaglioRepository;

    public StoricoGeneraleService(
            StoricoGeneraleRepository storicoGeneraleRepository,
            StoricoDettaglioRepository storicoDettaglioRepository) {
        this.storicoGeneraleRepository = storicoGeneraleRepository;

        this.storicoDettaglioRepository = storicoDettaglioRepository;
    }

    public Boolean elimina(long id) {
        Optional<StoricoGenerale> storicoGenerale = storicoGeneraleRepository.findById(
                id);
        boolean risposta = storicoGenerale.isPresent();
        if (risposta) {
            if (!storicoGenerale.get().getStoricoDettaglioList().isEmpty()) {
                for (StoricoDettaglio storicoDettaglio : storicoGenerale.get()
                        .getStoricoDettaglioList()) {
                    storicoDettaglioRepository.deleteById(storicoDettaglio.getIdStoricoDettaglio());
                }
            }
            storicoGeneraleRepository.deleteById(id);
        }
        return risposta;
    }

    public StoricoGeneraleDto aggiornaNote(Long idStoricoGenerale, String note) {
        Optional<StoricoGenerale> storicoGeneraleOptional = storicoGeneraleRepository.findById(
                idStoricoGenerale);
        if (storicoGeneraleOptional.isEmpty()) {
            throw new RuntimeException(
                    "non esiste storico con questo id");
        }
        StoricoGenerale storicoGenerale = storicoGeneraleOptional.get();
        storicoGenerale.setNote(note);
        storicoGenerale = storicoGeneraleRepository.save(storicoGenerale);

        return fromModelToDto(storicoGenerale);
    }

    public Optional<StoricoGeneraleDto> ricercaId(long id) {
        Optional<StoricoGenerale> storicoGeneraleTrovato = storicoGeneraleRepository.findById(
                id);
        if (storicoGeneraleTrovato.isEmpty()) {
            throw new RuntimeException(
                    "non esiste Storico Generale con questo ID");
        }
        return storicoGeneraleTrovato.map(this::fromModelToDto);
    }

    public Optional<StoricoGenerale> ricercaIdModel(long id) {
        Optional<StoricoGenerale> storicoGeneraleTrovato = storicoGeneraleRepository.findById(
                id);
        if (storicoGeneraleTrovato.isEmpty()) {
            throw new RuntimeException(
                    "non esiste Storico Generale con questo ID");
        }
        return storicoGeneraleTrovato;
    }

    public StoricoGeneraleDto fromModelToDto(StoricoGenerale oggettoDaTrasformare) {
        return StoricoGeneraleDto.builder()
                .idStorico(oggettoDaTrasformare.getIdStorico())
                .idBagno(oggettoDaTrasformare.getBagno().getIdBagno())
                .nomeBagno(oggettoDaTrasformare.getBagno().getNome())
                .scattiTotali(oggettoDaTrasformare.getScattiTotali())
                .restoScatti(oggettoDaTrasformare.getRestoScatti())
                .scattiInseriti(oggettoDaTrasformare.getScattiInseriti())
                .idAlimentazione(oggettoDaTrasformare.getAlimentazione()
                        .getIdAlimentazione())
                .dataCreazione(oggettoDaTrasformare.getDataCreazione())
                .dataFine(oggettoDaTrasformare.getDataFine())
                .dataControlloTempo(oggettoDaTrasformare.getDataControlloTempo())
                .concluso(oggettoDaTrasformare.getConcluso())
                .annullato(oggettoDaTrasformare.getAnnullato())
                .tipologiaAggiunta(oggettoDaTrasformare.getTipologiaAggiunta())
                .moltiplicatoreAlimentazione(oggettoDaTrasformare.getMoltiplicatoreAlimentazione())
                .note(oggettoDaTrasformare.getNote())
                .build();
    }
}
