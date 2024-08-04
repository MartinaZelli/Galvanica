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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static org.galvanica.math.ConvertitoreUnitaMisura.convertiQuantitaGenerico;
import static org.galvanica.math.ConvertitoreUnitaMisura.convertiUnitaMisuraPerDto;

@Service
public class RicercaPerDataService {

    /*viene richiesta una specifica data o un range di date.
viene quindi visualizzato, diviso per card di ogni bagno, le aggiunte ancora da gestire.
possono essere gestite come per ricerca del bagno gestendo le aggiunte:
complessivamente, per bagno oppure per singola aggiunta.
anche qui grande pulsante salva e avviso prima di reindirizzamento e/o salvataggio.
*/

    private final StoricoDettaglioRepository storicoDettaglioRepository;
    private final BagnoService bagnoService;

    public RicercaPerDataService(
            StoricoDettaglioRepository storicoDettaglioRepository,
            BagnoService bagnoService) {
        this.storicoDettaglioRepository = storicoDettaglioRepository;
        this.bagnoService = bagnoService;
    }

    public List<StoricoTotaleSingoloDto> mostraAggiunteDaGestireByDate(
            LocalDate dataInizio, LocalDate dataFine) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByDate(
                        dataInizio,
                        dataFine);
        if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
            return new ArrayList<>();
        }
        List<StoricoTotaleSingoloDto> lista = new ArrayList<>();
        for (StoricoDettaglio dettaglio : storicoDettaglioList) {
            lista.add(storicoTotaleSingoloDtoBuilder(dettaglio));
        }
        return lista;
    }

    public List<BagnoDto> selezionaBagno() {
        return bagnoService.findAllBagno();
    }

    public List<StoricoTotaleSingoloDto> mostraAggiunteDaGestireByDateEBagno(
            LocalDate dataInizio, LocalDate dataFine, Long idBagno) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByDateEBagno(
                        dataInizio,
                        dataFine,
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

    private Map<Long, List<StoricoDettaglio>> gestioneMappePerMostraAggiunteGroupBagnoSingolo(
            List<StoricoDettaglio> storicoDettaglioList) {
        Map<Long, List<StoricoDettaglio>> mappa = new HashMap<>();
        for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {
            Long key = storicoDettaglio.getProdotto().getIdProdotto();
            if (mappa.containsKey(key)) {
                mappa.get(key).add(storicoDettaglio);
            }
            if (!mappa.containsKey(key)) {
                mappa.put(storicoDettaglio.getProdotto().getIdProdotto(),
                        new ArrayList<>(
                                Arrays.asList(storicoDettaglio)));
            }
        }
        return mappa;
    }

    public List<StoricoTotaleGroupDto> mostraAggiunteDaGestireGroupByDateeBagno(
            LocalDate dataInizio, LocalDate dataFine, Long idBagno) {
        List<StoricoDettaglio> storicoDettaglioList =
                storicoDettaglioRepository.listaStoriciDettagliDaGestireByDateEBagno(
                        dataInizio,
                        dataFine,
                        idBagno);
        if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, List<StoricoDettaglio>> mappaProdottoEListaId =
                gestioneMappePerMostraAggiunteGroupBagnoSingolo(storicoDettaglioList);
        Map<Long, StoricoTotaleGroupDto> mappaRisposta = new HashMap<>();

        for (Map.Entry<Long, List<StoricoDettaglio>> entry : mappaProdottoEListaId.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }

        }

        for (StoricoDettaglio storicoDettaglio : storicoDettaglioList) {
            Long key = storicoDettaglio.getProdotto().getIdProdotto();
            if (mappaRisposta.containsKey(key)) {
                Double quantitaProdotto = mappaRisposta.get(key)
                        .getQuantitaProdotto()
                        + storicoDettaglio.getQuantita();
                mappaRisposta.get(key).setQuantitaProdotto(quantitaProdotto);

                mappaRisposta.get(key)
                        .getIdStoricoDettaglioList()
                        .add(storicoDettaglio.getIdStoricoDettaglio());
            }
            if (!mappaRisposta.containsKey(key)) {
                mappaRisposta.put(storicoDettaglio.getProdotto().getIdProdotto(),
                        storicoTotaleGroupDtoBuilder(storicoDettaglio));
            }
        }
        for (StoricoTotaleGroupDto storico : mappaRisposta.values()) {

            UnitaDiMisura unitaDiMisura = convertiUnitaMisuraPerDto(
                    (int) Math.round(storico.getQuantitaProdotto()),
                    storico.getUnitaDiMisura().isSonoVolume());
            Double quantita = convertiQuantitaGenerico(
                    storico.getQuantitaProdotto(),
                    storico.getUnitaDiMisura(),
                    unitaDiMisura);
            mappaRisposta.get(storico.getIdProdotto()).setQuantitaProdotto(quantita);
            mappaRisposta.get(storico.getIdProdotto())
                    .setUnitaDiMisura(unitaDiMisura);

        }

        return new ArrayList<>(mappaRisposta.values());
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
