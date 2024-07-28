package org.galvanica.service.gestisciAggiunte;

import org.galvanica.dto.StoricoTotaleGroupDto;
import org.galvanica.dto.StoricoTotaleSingoloDto;
import org.galvanica.dto.dtoConModel.BagnoDto;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.model.Alimentazione;
import org.galvanica.model.Bagno;
import org.galvanica.model.StoricoDettaglio;
import org.galvanica.model.StoricoGenerale;
import org.galvanica.repository.StoricoDettaglioRepository;
import org.galvanica.service.CRUD.BagnoService;
import org.galvanica.service.operazioniBagno.StoriciAnnullaOConcludiService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.galvanica.math.ConvertitoreUnitaMisura.convertiQuantitaGenerico;
import static org.galvanica.math.ConvertitoreUnitaMisura.convertiUnitaMisuraPerDto;

@Service
public class RicercaPerBagnoService {

    private final BagnoService bagnoService;
    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private final StoriciAnnullaOConcludiService storiciAnnullaOConcludiService;

    public RicercaPerBagnoService(BagnoService bagnoService,
                                  StoricoDettaglioRepository storicoDettaglioRepository,
                                  StoriciAnnullaOConcludiService storiciAnnullaOConcludiService) {
        this.bagnoService = bagnoService;
        this.storicoDettaglioRepository = storicoDettaglioRepository;
        this.storiciAnnullaOConcludiService = storiciAnnullaOConcludiService;
    }

    public List<BagnoDto> selezionaBagno() {
        return bagnoService.findAllBagno();
    }

    public List<StoricoTotaleSingoloDto> mostraAggiunteDaGestireByBagno(
            Long idBagno) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByBagno(
                        idBagno);
        if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
            return new ArrayList<>();
        }
        List<StoricoTotaleSingoloDto> lista = new ArrayList<>();
        for (StoricoDettaglio dettaglio : storicoDettaglioList) {
            lista.add(storicoTotaleSingoloDtoBuilder(dettaglio));
        }
        return lista;
    }

    public List<StoricoTotaleGroupDto> mostraAggiunteDaGestireGroupByBagno(
            Long idBagno) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByBagno(
                        idBagno);
        if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, StoricoTotaleGroupDto> mappa = new HashMap<>();
        for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {
            Long key = storicoDettaglio.getProdotto().getIdProdotto();
            if (mappa.containsKey(key)) {
                Double quantitaProdotto = mappa.get(key).getQuantitaProdotto()
                        + storicoDettaglio.getQuantita();
                mappa.get(key).setQuantitaProdotto(quantitaProdotto);

                mappa.get(key)
                        .getIdStoricoDettaglioList()
                        .add(storicoDettaglio.getIdStoricoDettaglio());
            }
            if (!mappa.containsKey(key)) {
                mappa.put(storicoDettaglio.getProdotto()
                                .getIdProdotto(),
                        storicoTotaleGroupDtoBuilder(storicoDettaglio));
            }
        }
        for (StoricoTotaleGroupDto storico : mappa.values()) {

            UnitaDiMisura unitaDiMisura = convertiUnitaMisuraPerDto(
                    (int) Math.round(storico.getQuantitaProdotto()),
                    storico.getUnitaDiMisura().isSonoVolume());
            Double quantita = convertiQuantitaGenerico(
                    storico.getQuantitaProdotto(),
                    storico.getUnitaDiMisura(),
                    unitaDiMisura);
            mappa.get(storico.getIdProdotto()).setQuantitaProdotto(quantita);
            mappa.get(storico.getIdProdotto()).setUnitaDiMisura(unitaDiMisura);

        }

        return new ArrayList<>(mappa.values());
    }

    public void eseguiListaAggiunte(List<Long> idDettaglioList) {
        for (Long id : idDettaglioList) {
            storiciAnnullaOConcludiService.eseguiSingolaAggiunta(id);
        }
    }

    public void eseguiTutteAggiunteByBagno(Long idBagno) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByBagno(
                        idBagno);
        if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
            return;
        }
        for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {
            storiciAnnullaOConcludiService
                    .eseguiSingolaAggiunta(storicoDettaglio.getIdStoricoDettaglio());
        }
    }

    public void annullaListaAggiunte(List<Long> idDettaglioList) {
        for (Long id : idDettaglioList) {
            storiciAnnullaOConcludiService.escludiSingolaAggiunta(id);
        }
    }

    public void annullaTutteAggiunteByBagno(Long idBagno) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByBagno(
                        idBagno);
        if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
            return;
        }
        for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {
            storiciAnnullaOConcludiService
                    .escludiSingolaAggiunta(storicoDettaglio.getIdStoricoDettaglio());
        }
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

    private StoricoTotaleGroupDto storicoTotaleGroupDtoBuilder(
            StoricoDettaglio dettaglio) {
        List<Long> idStoricoDettaglioList = new ArrayList<>();
        idStoricoDettaglioList.add(dettaglio.getIdStoricoDettaglio());

        return StoricoTotaleGroupDto.builder()
                .idBagno(dettaglio.getStoricoGenerale().getBagno().getIdBagno())
                .nomeBagno(dettaglio.getStoricoGenerale().getBagno().getNome())
                .idAlimentazione(dettaglio.getStoricoGenerale().getAlimentazione()
                        .getIdAlimentazione())
                .tipologiaAggiunta(dettaglio.getStoricoGenerale()
                        .getTipologiaAggiunta())
                .idProdotto(dettaglio.getProdotto().getIdProdotto())
                .nomeProdotto(dettaglio.getProdotto().getNome())
                .quantitaProdotto((double) dettaglio.getQuantita())
                .unitaDiMisura(dettaglio.getUnitaDiMisura())
                .idStoricoDettaglioList(idStoricoDettaglioList)
                .build();
    }

    //Viene richiesto di selezionare un bagno specifico.

    //A questo punto verranno visualizzate tutte le aggiunte non ancora gestite per ordine:
    //prodotto, quantità, unità di misura, annulla, conferma, note, data

    //In alto vi sarà un bottone con un flag per “group by prodotto”
    // che sommerà tutte le aggiunte dello stesso prodotto (esempio: 200 ml di A + 300 ml di B + 200 ml di A diventano: 400 ml di A + 300 ml di B)  tale pulsante elimina il campo note e data.

    //sulla tabella, in alto, si potrà flaggare in la colonna annulla o conferma per selezionare su tutte le aggiunte “annulla” o “conferma”.
    //accanto ad ogni aggiunta vi sarà un flag per annullare o confermare le aggiunte singolarmente
    //a fondo pagina un pulsante grande “Salva”.
    //prima del salvataggio (o di un reindirizzamento della pagina) sarebbe bene far apparire un messaggio di avviso.
}
