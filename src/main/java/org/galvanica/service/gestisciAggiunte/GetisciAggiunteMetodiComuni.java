package org.galvanica.service.gestisciAggiunte;

import org.galvanica.dto.StoricoTotaleGroupDto;
import org.galvanica.dto.StoricoTotaleSingoloDto;
import org.galvanica.math.UnitaDiMisura;
import org.galvanica.model.Alimentazione;
import org.galvanica.model.Bagno;
import org.galvanica.model.StoricoDettaglio;
import org.galvanica.model.StoricoGenerale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.galvanica.math.ConvertitoreUnitaMisura.convertiQuantitaGenerico;
import static org.galvanica.math.ConvertitoreUnitaMisura.convertiUnitaMisuraPerDto;

public class GetisciAggiunteMetodiComuni {
    public static List<StoricoTotaleSingoloDto> getStoricoTotaleSingoloDtoList(
            List<StoricoDettaglio> storicoDettaglioList) {
        if (storicoDettaglioList == null || storicoDettaglioList.isEmpty()) {
            return new ArrayList<>();
        }
        List<StoricoTotaleSingoloDto> lista = new ArrayList<>();
        for (StoricoDettaglio dettaglio : storicoDettaglioList) {
            lista.add(storicoTotaleSingoloDtoBuilder(dettaglio));
        }
        return lista;
    }

    private static StoricoTotaleSingoloDto storicoTotaleSingoloDtoBuilder(
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

    public static List<StoricoTotaleGroupDto> getStoricoTotaleGroupDtoList(
            List<StoricoDettaglio> storicoDettaglioList) {
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
                mappa.put(storicoDettaglio.getProdotto().getIdProdotto(),
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

    private static StoricoTotaleGroupDto storicoTotaleGroupDtoBuilder(
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

    public static List<StoricoTotaleGroupDto> queryTransformerByDate(
            List<Map<String, Object>> listaQuery) {
        List<StoricoTotaleGroupDto> storicoTotaleGroupDtoList = new ArrayList<>();
        for (Map<String, Object> query : listaQuery) {
            StoricoTotaleGroupDto storicoTotaleGroupDto =
                    StoricoTotaleGroupDto.builder().build();
            if (query.containsKey("idBagno")) {
                storicoTotaleGroupDto.setIdBagno((Long) query.get("idBagno"));
            }
            if (query.containsKey("nomeBagno")) {
                storicoTotaleGroupDto.setNomeBagno((String) query.get("nomeBagno"));
            }
            if (query.containsKey("quantitaProdotto")) {
                storicoTotaleGroupDto
                        .setQuantitaProdotto((Double) query.get("quantitaProdotto"));
            }
            if (query.containsKey("unitaDiMisura")) {
                storicoTotaleGroupDto
                        .setUnitaDiMisura((UnitaDiMisura) query.get("unitaDiMisura"));
            }
            if (query.containsKey("idProdotto")) {
                storicoTotaleGroupDto.setIdProdotto((Long) query.get("idProdotto"));
            }
            if (query.containsKey("idAlimentazione")) {
                storicoTotaleGroupDto.setIdAlimentazione((Long) query.get(
                        "idAlimentazione"));
            }
            if (query.containsKey("idStoricoDettaglioList")) {
                List<Long> idStoricoDettaglioList = new ArrayList<>();
                String idString = (String) query.get("idStoricoDettaglioList");
                String[] idArray = idString.split(",");
                for (String s : idArray) {
                    idStoricoDettaglioList.add(Long.parseLong(s));
                }
                storicoTotaleGroupDto.setIdStoricoDettaglioList(
                        idStoricoDettaglioList);
            }
            storicoTotaleGroupDtoList.add(storicoTotaleGroupDto);
        }

        return storicoTotaleGroupDtoList;
    }
}
