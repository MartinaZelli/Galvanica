package org.galvanica.service.CRUD;

import org.galvanica.dto.dtoConModel.DettaglioAlimentazioneDto;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.model.Alimentazione;
import org.galvanica.model.DettaglioAlimentazione;
import org.galvanica.model.Prodotto;
import org.galvanica.repository.AlimentazioneRepository;
import org.galvanica.repository.DettaglioAlimentazioneRepository;
import org.galvanica.repository.ProdottoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.galvanica.math.ConvertitoreUnitaMisura.*;


@Service
public class DettaglioAlimentazioneService implements ICRUDService<DettaglioAlimentazioneDto, DettaglioAlimentazione> {

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
        validaDettaglioAlimentazione(elemento);
        Alimentazione alimentazione = alimentazioneRepository.findById(
                elemento.getIdAlimentazione()).orElseThrow();
        Prodotto prodotto = prodottoRepository.findById(elemento.getIdProdotto())
                .orElseThrow();

        if (alimentazione
                .getDettaglioAlimentazioneList()
                .stream()
                .anyMatch(dettaglioAlimentazione -> Objects.equals(
                        dettaglioAlimentazione.getProdotto().getIdProdotto(),
                        elemento.getIdProdotto()))) {
            throw new RuntimeException(
                    "il prodotto è già inserito nel dettaglio alimentazione. " +
                            "Si prega di modificare o eliminare quello precedente.");
        }
        DettaglioAlimentazione dettaglioAlimentazione = DettaglioAlimentazione.builder()
                .note(elemento.getNote())
                .prodotto(prodotto)
                .alimentazione(alimentazione)
                .build();
        return aggiornamentoDataBase(elemento, dettaglioAlimentazione);

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
    public DettaglioAlimentazioneDto aggiorna(DettaglioAlimentazioneDto elemento) {
        Optional<DettaglioAlimentazione> dettaglioAlimentazioneOptional = dettaglioAlimentazioneRepository.findById(
                elemento.getIdDettaglio());
        if (dettaglioAlimentazioneOptional.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        validaDettaglioAlimentazione(elemento);
        Alimentazione alimentazione = alimentazioneRepository.findById(
                elemento.getIdAlimentazione()).orElseThrow();
        Prodotto prodotto = prodottoRepository.findById(elemento.getIdProdotto())
                .orElseThrow();

        DettaglioAlimentazione dettaglioAlimentazione = dettaglioAlimentazioneOptional.get();
        dettaglioAlimentazione.setNote(elemento.getNote());
        dettaglioAlimentazione.setProdotto(prodotto);
        dettaglioAlimentazione.setAlimentazione(alimentazione);
        return aggiornamentoDataBase(elemento, dettaglioAlimentazione);
    }

    @Override
    public Optional<DettaglioAlimentazioneDto> ricercaId(long id) {
        Optional<DettaglioAlimentazione> dettaglioAlimentazioneOptional =
                dettaglioAlimentazioneRepository.findById(id);
        return dettaglioAlimentazioneOptional
                .map(this::fromModelToDto);
    }

    @Override
    public DettaglioAlimentazioneDto fromModelToDto(
            DettaglioAlimentazione oggettoDaTrasformare) {
        Double quantitaProdotto = convertiQuantitaPerDto(
                oggettoDaTrasformare.getQuantitaProdotto(),
                oggettoDaTrasformare.getUnitaDiMisura().isSonoVolume());
        UnitaDiMisura unita = convertiUnitaMisuraPerDto(
                oggettoDaTrasformare.getQuantitaProdotto(),
                oggettoDaTrasformare.getUnitaDiMisura().isSonoVolume());
        return DettaglioAlimentazioneDto.builder()
                .idDettaglio(oggettoDaTrasformare.getIdDettaglio())
                .note(oggettoDaTrasformare.getNote())
                .quantitaProdotto(quantitaProdotto)
                .unitaDiMisura(unita)
                .idProdotto(oggettoDaTrasformare.getProdotto().getIdProdotto())
                .idAlimentazione(oggettoDaTrasformare.getAlimentazione()
                        .getIdAlimentazione())
                .nomeProdotto(oggettoDaTrasformare.getProdotto().getNome())
                .nomeBagno(oggettoDaTrasformare.getAlimentazione()
                        .getBagno()
                        .getNome())
                .build();
    }

    public List<DettaglioAlimentazioneDto> findAllDettaglioAlimentazione() {
        return StreamSupport.stream(dettaglioAlimentazioneRepository.findAll()
                        .spliterator(),
                false).map(this::fromModelToDto).collect(
                Collectors.toList());
    }

    public List<DettaglioAlimentazioneDto> ricercaDettaglioAlimentazioneByAlimentazione(
            Long idAlimentazione) {
        Optional<Alimentazione> alimentazioneOptional = alimentazioneRepository.findById(
                idAlimentazione);
        if (alimentazioneOptional.isEmpty()) {
            throw new RuntimeException(
                    "non esiste alimentazione con questo ID");
        }
        if (alimentazioneOptional.get().getDettaglioAlimentazioneList() == null) {
            return new ArrayList<>();
        }
        return alimentazioneOptional.get()
                .getDettaglioAlimentazioneList()
                .stream()
                .map(this::fromModelToDto)
                .toList();
    }

    private void validaDettaglioAlimentazione(DettaglioAlimentazioneDto elemento) {
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
        if (elemento.getUnitaDiMisura().isSonoVolume() != prodottoOptional.get()
                .getSonoVolume()) {
            throw new RuntimeException(
                    "l'unità di misura selezionata non è dello stesso tipo dell'unità di misura del prodotto.");
        }
    }

    private DettaglioAlimentazioneDto aggiornamentoDataBase(
            DettaglioAlimentazioneDto elemento,
            DettaglioAlimentazione dettaglioAlimentazione) {
        UnitaDiMisura unita = UnitaDiMisura.MG;
        if (elemento.getUnitaDiMisura().isSonoVolume()) {
            unita = UnitaDiMisura.ML;
        }
        Integer quantita = convertiQuantitaToDatabase(elemento.getQuantitaProdotto(),
                elemento.getUnitaDiMisura());
        dettaglioAlimentazione.setUnitaDiMisura(unita);
        dettaglioAlimentazione.setQuantitaProdotto(quantita);
        dettaglioAlimentazione = dettaglioAlimentazioneRepository.save(
                dettaglioAlimentazione);
        return fromModelToDto(dettaglioAlimentazione);
    }
}
