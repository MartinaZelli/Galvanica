package org.galvanica.service.CRUD;

import org.galvanica.dto.dtoConModel.DettaglioAlimentazioneDto;
import org.galvanica.model.Alimentazione;
import org.galvanica.model.DettaglioAlimentazione;
import org.galvanica.model.Prodotto;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.DettaglioAlimentazioneRepository;
import org.galvanica.repository.ProdottoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DettaglioAlimentazioneService implements ICRUDService<DettaglioAlimentazioneDto> {

    private final DettaglioAlimentazioneRepository dettaglioAlimentazioneRepository;
    private final ProdottoRepository prodottoRepository;
    private final AlimentazioneRepository alimentazioneRepository;

    public DettaglioAlimentazioneService(
            DettaglioAlimentazioneRepository dettaglioAlimentazioneRepository,
            ProdottoRepository prodottoRepository,
            AlimentazioneRepository alimentazioneRepository) {
        this.dettaglioAlimentazioneRepository = dettaglioAlimentazioneRepository;
        this.prodottoRepository = prodottoRepository;
        this.alimentazioneRepository = alimentazioneRepository;
    }

    @Override
    public DettaglioAlimentazioneDto inserisci(DettaglioAlimentazioneDto elemento) {
        if (elemento.getIdDettaglio() != null) {
            throw new RuntimeException(
                    "l'id deve essere autoincrementale, non inizializzare");
        }
        if (elemento.getIdAlimentazione() == null) {
            throw new RuntimeException(
                    "l'id alimentazione deve essere valorizzato");
        }
        if (elemento.getIdProdotto() == null) {
            throw new RuntimeException(
                    "l'id prodotto deve essere valorizzato");
        }
        if (elemento.getQuantitaProdotto() != null && elemento.getUnitaDiMisura() == null) {
            throw new RuntimeException(
                    "se è inizializzata la quantità di prodotto inserire anche l'unità di misura.");
        }
        Optional<Alimentazione> alimentazioneOptional = alimentazioneRepository.findById(
                elemento.getIdAlimentazione());
        if (alimentazioneOptional.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id di alimentazione corretto");
        }
        Optional<Prodotto> prodottoOptional = prodottoRepository.findById(elemento.getIdProdotto());
        if (prodottoOptional.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id prodotto corretto");
        }
        DettaglioAlimentazione dettaglioAlimentazione = DettaglioAlimentazione.builder()
                .note(elemento.getNote())
                .quantitaProdotto(elemento.getQuantitaProdotto())
                .unitaDiMisura(elemento.getUnitaDiMisura())
                .prodotto(prodottoOptional.get())
                .alimentazione(alimentazioneOptional.get())
                .build();
        dettaglioAlimentazione = dettaglioAlimentazioneRepository.save(
                dettaglioAlimentazione);
        return DettaglioAlimentazioneDto.builder()
                .idDettaglio(dettaglioAlimentazione.getIdDettaglio())
                .note(dettaglioAlimentazione.getNote())
                .quantitaProdotto(dettaglioAlimentazione.getQuantitaProdotto())
                .unitaDiMisura(dettaglioAlimentazione.getUnitaDiMisura())
                .idProdotto(dettaglioAlimentazione.getProdotto().getIdProdotto())
                .idAlimentazione(dettaglioAlimentazione.getAlimentazione()
                        .getIdAlimentazione())
                .build();
    }

    @Override
    public Boolean elimina(long id) {
        boolean risposta = dettaglioAlimentazioneRepository.findById(id).isPresent();
        if (risposta) {
            dettaglioAlimentazioneRepository.deleteById(id);
        }
        return risposta;
    }

    @Override
    public DettaglioAlimentazioneDto aggiorna(DettaglioAlimentazioneDto elemento,
                                              long id) {
        Optional<DettaglioAlimentazione> dettaglioAlimentazioneOptional = dettaglioAlimentazioneRepository.findById(
                id);
        if (dettaglioAlimentazioneOptional.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        if (elemento.getIdAlimentazione() == null) {
            throw new RuntimeException(
                    "l'id alimentazione deve essere valorizzato");
        }
        if (elemento.getIdProdotto() == null) {
            throw new RuntimeException(
                    "l'id prodotto deve essere valorizzato");
        }
        if (elemento.getQuantitaProdotto() != null && elemento.getUnitaDiMisura() == null) {
            throw new RuntimeException(
                    "se è inizializzata la quantità di prodotto inserire anche l'unità di misura.");
        }
        Optional<Alimentazione> alimentazioneOptional = alimentazioneRepository.findById(
                elemento.getIdAlimentazione());
        if (alimentazioneOptional.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id di alimentazione corretto");
        }
        Optional<Prodotto> prodottoOptional = prodottoRepository.findById(elemento.getIdProdotto());
        if (prodottoOptional.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id prodotto corretto");
        }
        DettaglioAlimentazione dettaglioAlimentazione = dettaglioAlimentazioneOptional.get();
        dettaglioAlimentazione.setNote(elemento.getNote());
        dettaglioAlimentazione.setQuantitaProdotto(elemento.getQuantitaProdotto());
        dettaglioAlimentazione.setUnitaDiMisura(elemento.getUnitaDiMisura());
        dettaglioAlimentazione.setProdotto(prodottoOptional.get());
        dettaglioAlimentazione.setAlimentazione(alimentazioneOptional.get());
        dettaglioAlimentazione = dettaglioAlimentazioneRepository.save(
                dettaglioAlimentazione);

        return DettaglioAlimentazioneDto.builder()
                .idDettaglio(dettaglioAlimentazione.getIdDettaglio())
                .note(dettaglioAlimentazione.getNote())
                .quantitaProdotto(dettaglioAlimentazione.getQuantitaProdotto())
                .unitaDiMisura(dettaglioAlimentazione.getUnitaDiMisura())
                .idProdotto(dettaglioAlimentazione.getProdotto().getIdProdotto())
                .idAlimentazione(dettaglioAlimentazione.getAlimentazione()
                        .getIdAlimentazione())
                .build();
    }

    @Override
    public Optional<DettaglioAlimentazioneDto> ricercaId(long id) {
        Optional<DettaglioAlimentazione> dettaglioAlimentazioneOptional =
                dettaglioAlimentazioneRepository.findById(id);
        return dettaglioAlimentazioneOptional
                .map(dettaglioAlimentazione -> DettaglioAlimentazioneDto.builder()
                        .idDettaglio(dettaglioAlimentazione.getIdDettaglio())
                        .note(dettaglioAlimentazione.getNote())
                        .quantitaProdotto(dettaglioAlimentazione.getQuantitaProdotto())
                        .unitaDiMisura(dettaglioAlimentazione.getUnitaDiMisura())
                        .idProdotto(dettaglioAlimentazione.getProdotto()
                                .getIdProdotto())
                        .idAlimentazione(dettaglioAlimentazione.getAlimentazione()
                                .getIdAlimentazione())
                        .build());
    }
}
