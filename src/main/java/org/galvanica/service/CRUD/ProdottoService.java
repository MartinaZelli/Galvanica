package org.galvanica.service.CRUD;

import org.galvanica.dto.dtoConModel.ProdottoDto;
import org.galvanica.model.*;
import org.galvanica.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProdottoService implements ICRUDService<ProdottoDto, Prodotto> {

    private final ProdottoRepository prodottoRepository;
    private final MagazzinoRepository magazzinoRepository;
    private final BagnoRepository bagnoRepository;
    private final DettaglioAlimentazioneRepository dettaglioAlimentazioneRepository;
    private final AlimentazioneRepository alimentazioneRepository;

    public ProdottoService(ProdottoRepository prodottoRepository,
                           MagazzinoRepository magazzinoRepository,
                           BagnoRepository bagnoRepository,
                           DettaglioAlimentazioneRepository dettaglioAlimentazioneRepository,
                           AlimentazioneRepository alimentazioneRepository) {
        this.prodottoRepository = prodottoRepository;
        this.magazzinoRepository = magazzinoRepository;
        this.bagnoRepository = bagnoRepository;

        this.dettaglioAlimentazioneRepository = dettaglioAlimentazioneRepository;
        this.alimentazioneRepository = alimentazioneRepository;
    }

    @Override
    public ProdottoDto inserisci(ProdottoDto elemento) {
        if (elemento.getIdProdotto() != null) {
            throw new RuntimeException(
                    "l'id deve essere autoincrementale, non inizializzare");
        }
        if (elemento.getIdMagazzino() == null) {
            throw new RuntimeException(
                    "l'id magazzino deve essere valorizzato");
        }
        Magazzino magazzino = validaProdotto(elemento);
        Prodotto prodotto = Prodotto.builder()
                .descrizione(elemento.getDescrizione())
                .nome(elemento.getNome())
                .magazzino(magazzino)
                .ph(elemento.getPh())
                .build();
        prodotto = prodottoRepository.save(prodotto);
        return fromModelToDto(prodotto);
    }

    @Override
    public Boolean elimina(long id) {
        boolean risposta = prodottoRepository.findById(id).isPresent();
        if (risposta) {
            prodottoRepository.deleteById(id);
        }
        return risposta;
    }

    @Override
    public ProdottoDto aggiorna(ProdottoDto elemento) {
        Optional<Prodotto> prodottoOptional = prodottoRepository.findById(elemento.getIdProdotto());
        if (prodottoOptional.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        if (elemento.getIdMagazzino() == null) {
            throw new RuntimeException(
                    "il magazzino non può essere null");
        }
        Magazzino magazzino = validaProdotto(elemento);

        Prodotto prodotto = prodottoOptional.get();
        prodotto.setMagazzino(magazzino);
        prodotto.setNome(elemento.getNome());
        prodotto.setDescrizione(elemento.getDescrizione());
        prodotto.setPh(elemento.getPh());
        prodotto = prodottoRepository.save(prodotto);

        return fromModelToDto(prodotto);

    }

    private Magazzino validaProdotto(ProdottoDto elemento) {
        Optional<Magazzino> magazzinoTrovato = magazzinoRepository.findById(elemento.getIdMagazzino());
        if (magazzinoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "l'id del magazzino non esiste, correggere.");
        }
        if (elemento.getPh() != null) {
            if (elemento.getPh() < 0 || elemento.getPh() > 14) {
                throw new RuntimeException(
                        "il pH ha valori compresi fra 0 e 14 se inizializzato.");
            }
        }
        return magazzinoTrovato.get();
    }

    @Override
    public Optional<ProdottoDto> ricercaId(long id) {
        Optional<Prodotto> prodottoOptional = prodottoRepository.findById(id);
        return prodottoOptional
                .map(this::fromModelToDto);
    }

    @Override
    public ProdottoDto fromModelToDto(Prodotto oggettoDaTrasformare) {
        return ProdottoDto.builder()
                .idProdotto(oggettoDaTrasformare.getIdProdotto())
                .idMagazzino(oggettoDaTrasformare.getMagazzino().getIdMagazzino())
                .descrizioneMagazzino(oggettoDaTrasformare.getMagazzino()
                        .getDescrizione())
                .nome(oggettoDaTrasformare.getNome())
                .descrizione(oggettoDaTrasformare.getDescrizione())
                .ph(oggettoDaTrasformare.getPh())
                .build();
    }

    public List<ProdottoDto> findAllProdotto() {
        return StreamSupport.stream(prodottoRepository.findAll().spliterator(),
                false).map(this::fromModelToDto).collect(
                Collectors.toList());
    }

    public List<ProdottoDto> ricercaProdottiByBagno(Long idBagno) {
        Optional<Bagno> bagnoOptional = bagnoRepository.findById(idBagno);
        if (bagnoOptional.isEmpty()) {
            throw new RuntimeException(
                    "non esiste bagno con questo ID");
        }
        if (bagnoOptional.get().getRelazioneBagnoProdottoList() == null) {
            return new ArrayList<>();
        }
        return bagnoOptional.get()
                .getRelazioneBagnoProdottoList()
                .stream()
                .map(RelazioneBagnoProdotto::getProdotto)
                .map(this::fromModelToDto)
                .toList();
    }

    public List<ProdottoDto> ricercaProdottiInseribiliPerDettaglioAlimentazione(
            Long idDettagioAlimentazione) {
        Optional<DettaglioAlimentazione> dettaglioAlimentazioneOptional =
                dettaglioAlimentazioneRepository.findById(idDettagioAlimentazione);
        if (dettaglioAlimentazioneOptional.isEmpty()) {
            throw new RuntimeException(
                    "non esiste DettaglioAlimentazione con questo ID");
        }
        Optional<Alimentazione> alimentazione = alimentazioneRepository.findById(
                dettaglioAlimentazioneOptional.get()
                        .getAlimentazione().getIdAlimentazione());
        if (alimentazione.isEmpty()) {
            throw new RuntimeException(
                    "non esiste un Alimentazione con questo ID legata al DettaglioAlimentazione con questo ID");
        }
        return ricercaProdottiByBagno(alimentazione.get().getBagno().getIdBagno());

    }

    public List<ProdottoDto> ricercaProdottiInseribiliPerAlimentazione(
            Long idAlimentazione) {
        Optional<Alimentazione> alimentazione = alimentazioneRepository.findById(
                idAlimentazione);
        if (alimentazione.isEmpty()) {
            throw new RuntimeException(
                    "non esiste un Alimentazione con questo ID legata al DettaglioAlimentazione con questo ID");
        }
        return ricercaProdottiByBagno(alimentazione.get().getBagno().getIdBagno());

    }
}
