package org.galvanica.service.visualizza;

import org.galvanica.dto.StoricoTotaleSingoloDto;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.model.Alimentazione;
import org.galvanica.model.Bagno;
import org.galvanica.model.StoricoDettaglio;
import org.galvanica.model.StoricoGenerale;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.galvanica.math.ConvertitoreUnitaMisura.convertiQuantitaGenerico;
import static org.galvanica.math.ConvertitoreUnitaMisura.convertiUnitaMisuraPerDto;

@Service
public class ArchivioGeneraleService {
    private final StoricoDettaglioRepository storicoDettaglioRepository;

    public ArchivioGeneraleService(
            StoricoDettaglioRepository storicoDettaglioRepository) {
        this.storicoDettaglioRepository = storicoDettaglioRepository;
    }

    public List<StoricoTotaleSingoloDto> storicoTotaleSingoloDtoList() {
        return StreamSupport.stream(storicoDettaglioRepository.findAll()
                        .spliterator(), false)
                .map(this::storicoTotaleSingoloDtoBuilder)
                .collect(Collectors.toList());
    }

    private StoricoTotaleSingoloDto storicoTotaleSingoloDtoBuilder(
            StoricoDettaglio dettaglio) {
        StoricoGenerale generale = dettaglio.getStoricoGenerale();
        Bagno bagno = generale.getBagno();
        Alimentazione alimentazione = generale.getAlimentazione();
        if (alimentazione == null) {
            alimentazione = Alimentazione.builder()
                    .scatti(null)
                    .idAlimentazione(null)
                    .build();
        }
        UnitaDiMisura unitaDiMisura =
                convertiUnitaMisuraPerDto(dettaglio.getQuantita(),
                        dettaglio.getUnitaDiMisura().isSonoVolume());
        Double quantita =
                convertiQuantitaGenerico(Double.valueOf(dettaglio.getQuantita()),
                        dettaglio.getUnitaDiMisura(), unitaDiMisura);

        return StoricoTotaleSingoloDto.builder()
                .idBagno(bagno.getIdBagno())
                .nomeBagno(bagno.getNome())
                .quantitaProdotto(quantita)
                .unitaDiMisura(unitaDiMisura)
                .idProdotto(dettaglio.getProdotto().getIdProdotto())
                .nomeProdotto(dettaglio.getProdotto().getNome())
                .idAlimentazione(alimentazione.getIdAlimentazione())
                .tipologiaAggiunta(generale.getTipologiaAggiunta())
                .idStoricoGenerale(generale.getIdStorico())
                .idStoricoDettaglio(dettaglio.getIdStoricoDettaglio())
                .moltiplicatoreAlimentazione(generale.getMoltiplicatoreAlimentazione())
                .restoScattiBagno(generale.getRestoScattiBagno())
                .scattiAlimentazione(alimentazione.getScatti())
                .scattiTotaliBagno(generale.getScattiTotaliBagno())
                .scattiInseriti(generale.getScattiInseriti())
                //.restoScattiPrecedenti(null)
                .dataControlloTempo(generale.getDataControlloTempo())
                .eseguitoDettaglio(dettaglio.getEseguitoDettaglio())
                .annullatoDettaglio(dettaglio.getAnnullatoDettaglio())
                .noteStoricoGenerale(generale.getNote())
                .dataCreazione(generale.getDataCreazione())
                .build();
    }
}
