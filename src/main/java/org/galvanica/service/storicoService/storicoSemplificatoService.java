package org.galvanica.service.storicoService;

import org.galvanica.dto.StoricoSemplificatoDto;
import org.galvanica.math.ConvertitoreUnitaMisura;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class storicoSemplificatoService {
    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private ConvertitoreUnitaMisura convertitore;


    public storicoSemplificatoService(
            StoricoDettaglioRepository storicoDettaglioRepository) {
        this.storicoDettaglioRepository = storicoDettaglioRepository;
    }

    public List<StoricoSemplificatoDto> storicoSemplificatoDtoList(int limite) {
        List<StoricoSemplificatoDto> storicoSemplificatoDtoList = storicoDettaglioRepository.listaStoricoSemplificato(
                limite);

        for (StoricoSemplificatoDto storicoSemplificato : storicoSemplificatoDtoList) {
            UnitaDiMisura unitaDiMisura = convertitore.convertiUnitaMisuraPerDto(
                    storicoSemplificato.getQuantita().intValue(),
                    storicoSemplificato.getUnitaDiMisura().isSonoVolume());
            Double quantita = convertitore.convertiQuantitaGenerico(
                    storicoSemplificato.getQuantita(),
                    storicoSemplificato.getUnitaDiMisura(),
                    unitaDiMisura);
            storicoSemplificato.setUnitaDiMisura(unitaDiMisura);
            storicoSemplificato.setQuantita(quantita);
        }
        return storicoSemplificatoDtoList;
    }
}
