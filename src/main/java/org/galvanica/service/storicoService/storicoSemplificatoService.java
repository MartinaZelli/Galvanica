package org.galvanica.service.storicoService;

import org.galvanica.dto.StoricoTotaleDto;
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

    public List<StoricoTotaleDto> storicoSemplificatoDtoList(int limite) {
        List<StoricoTotaleDto> storicoTotaleDtoList = storicoDettaglioRepository.listaStoricoSemplificato(
                limite);

        for (StoricoTotaleDto storicoSemplificato : storicoTotaleDtoList) {
            UnitaDiMisura unitaDiMisura = convertitore.convertiUnitaMisuraPerDto(
                    storicoSemplificato.getQuantitaProdotto().intValue(),
                    storicoSemplificato.getUnitaDiMisura().isSonoVolume());
            Double quantita = convertitore.convertiQuantitaGenerico(
                    storicoSemplificato.getQuantitaProdotto(),
                    storicoSemplificato.getUnitaDiMisura(),
                    unitaDiMisura);
            storicoSemplificato.setUnitaDiMisura(unitaDiMisura);
            storicoSemplificato.setQuantitaProdotto(quantita);
        }
        return storicoTotaleDtoList;
    }
}
