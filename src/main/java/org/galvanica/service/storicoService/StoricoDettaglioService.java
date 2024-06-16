package org.galvanica.service.storicoService;

import org.galvanica.dto.dtoConModel.StoricoDettaglioDto;
import org.galvanica.math.ConvertitoreUnitaMisura;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.model.StoricoDettaglio;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.repository.StoricoGeneraleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StoricoDettaglioService {
    private final BagnoRepository bagnoRepository;
    private final StoricoGeneraleRepository storicoGeneraleRepository;
    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private ConvertitoreUnitaMisura convertitore;

    public StoricoDettaglioService(BagnoRepository bagnoRepository,
                                   StoricoGeneraleRepository storicoGeneraleRepository,
                                   StoricoDettaglioRepository storicoDettaglioRepository) {
        this.bagnoRepository = bagnoRepository;
        this.storicoGeneraleRepository = storicoGeneraleRepository;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
    }

    public Boolean elimina(long id) {
        Optional<StoricoDettaglio> storicoDettaglio = storicoDettaglioRepository.findById(
                id);
        boolean risposta = storicoDettaglio.isPresent();
        if (risposta) {
            storicoDettaglioRepository.deleteById(id);

        }
        return risposta;
    }

    public StoricoDettaglioDto aggiornaQuantitaProdotto(Long idStoricoDettaglio,
                                                        UnitaDiMisura unitaDiMisura,
                                                        Double quantita) {
        Optional<StoricoDettaglio> storicoDettaglioOptional = storicoDettaglioRepository.findById(
                idStoricoDettaglio);
        if (storicoDettaglioOptional.isEmpty()) {
            throw new RuntimeException(
                    "non esiste storico con questo id");
        }
        StoricoDettaglio storicoDettaglio = storicoDettaglioOptional.get();
        if (storicoDettaglio.getUnitaDiMisura()
                .isSonoVolume() != unitaDiMisura.isSonoVolume()) {
            throw new RuntimeException(
                    "non è possibile convertire un unità di misura di volume in peso e viceversa"
            );
        }
        Integer quantitaPerDB = convertitore.convertiQuantitaToDatabase(quantita,
                unitaDiMisura);
        UnitaDiMisura unitaDiMisuraPerDB = UnitaDiMisura.MG;
        if (unitaDiMisura.isSonoVolume()) {
            unitaDiMisuraPerDB = UnitaDiMisura.ML;
        }

        storicoDettaglio.setQuantita(quantitaPerDB);
        storicoDettaglio.setUnitaDiMisura(unitaDiMisuraPerDB);
        storicoDettaglio = storicoDettaglioRepository.save(storicoDettaglio);

        return fromModelToDto(storicoDettaglio);
    }


    public Optional<StoricoDettaglioDto> ricercaId(long id) {
        Optional<StoricoDettaglio> storicoDettaglioOptional = storicoDettaglioRepository.findById(
                id);
        if (storicoDettaglioOptional.isEmpty()) {
            throw new RuntimeException(
                    "non esiste Storico Generale con questo ID");
        }
        return storicoDettaglioOptional.map(this::fromModelToDto);
    }

    public Optional<StoricoDettaglio> ricercaIdModel(long id) {
        Optional<StoricoDettaglio> storicoDettaglioOptional = storicoDettaglioRepository.findById(
                id);
        if (storicoDettaglioOptional.isEmpty()) {
            throw new RuntimeException(
                    "non esiste Storico Generale con questo ID");
        }
        return storicoDettaglioOptional;
    }

    public StoricoDettaglioDto fromModelToDto(
            StoricoDettaglio oggettoDaTrasformare) {
        UnitaDiMisura unitaDiMisura = convertitore.convertiUnitaMisuraPerDto(
                oggettoDaTrasformare.getQuantita(),
                oggettoDaTrasformare.getUnitaDiMisura().isSonoVolume());
        Double quantita = convertitore.convertiQuantitaGenerico(oggettoDaTrasformare.getQuantita()
                        .doubleValue(),
                oggettoDaTrasformare.getUnitaDiMisura(),
                unitaDiMisura);

        return StoricoDettaglioDto.builder()
                .idStoricoGenerale(oggettoDaTrasformare.getStoricoGenerale()
                        .getIdStorico())
                .idStoricoDettaglio(oggettoDaTrasformare.getIdStoricoDettaglio())
                .idProdotto(oggettoDaTrasformare.getProdotto().getIdProdotto())
                .nomeProdotto(oggettoDaTrasformare.getProdotto().getNome())
                .quantita(quantita)
                .unitaDiMisura(unitaDiMisura)
                .eseguito(oggettoDaTrasformare.getEseguitoDettaglio())
                .escluso(oggettoDaTrasformare.getAnnullatoDettaglio())
                .build();
    }
}
