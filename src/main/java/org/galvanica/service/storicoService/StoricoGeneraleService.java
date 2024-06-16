package org.galvanica.service.storicoService;

import org.springframework.stereotype.Service;

@Service
public class StoricoGeneraleService {
//todo: riprendere metodi buoni e riportarli in nuova classe StoricoTotaleService
    /*
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
                .scattiTotaliBagno(oggettoDaTrasformare.getScattiTotaliBagno())
                .restoScattiBagno(oggettoDaTrasformare.getRestoScattiBagno())
                .scattiInseriti(oggettoDaTrasformare.getScattiInseriti())
                .idAlimentazione(oggettoDaTrasformare.getAlimentazione()
                        .getIdAlimentazione())
                .dataCreazione(oggettoDaTrasformare.getDataCreazione())
                .dataEsecuzione(oggettoDaTrasformare.getDataEsecuzione())
                .dataControlloTempo(oggettoDaTrasformare.getDataControlloTempo())
                .eseguitoGenerale(oggettoDaTrasformare.getEseguitoGenerale())
                .annullatoGenerale(oggettoDaTrasformare.getAnnullatoGenerale())
                .tipologiaAggiunta(oggettoDaTrasformare.getTipologiaAggiunta())
                .moltiplicatoreAlimentazione(oggettoDaTrasformare.getMoltiplicatoreAlimentazione())
                .note(oggettoDaTrasformare.getNote())
                .build();
    }
     */
}
