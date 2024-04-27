package org.galvanica.service.CRUD;

import org.galvanica.dto.dtoConModel.ProdottoDto;
import org.galvanica.model.Bagno;
import org.galvanica.model.Magazzino;
import org.galvanica.model.Prodotto;
import org.galvanica.model.RelazioneBagnoProdotto;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.MagazzinoRepository;
import org.galvanica.repository.ProdottoRepository;
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

    public ProdottoService(ProdottoRepository prodottoRepository,
                           MagazzinoRepository magazzinoRepository,
                           BagnoRepository bagnoRepository) {
        this.prodottoRepository = prodottoRepository;
        this.magazzinoRepository = magazzinoRepository;
        this.bagnoRepository = bagnoRepository;
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
        Optional<Magazzino> magazzinoTrovato = magazzinoRepository.findById(elemento.getIdMagazzino());
        if (magazzinoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "l'id del magazzino non esiste, correggere.");
        }
        if (elemento.getPh() < 0 || elemento.getPh() > 14) {
            throw new RuntimeException(
                    "il pH ha valori compresi fra 0 e 14 se inizializzato.");
        }
        Prodotto prodotto = Prodotto.builder()
                .descrizione(elemento.getDescrizione())
                .nome(elemento.getNome())
                .magazzino(magazzinoTrovato.get())
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
    public ProdottoDto aggiorna(ProdottoDto elemento, long id) {
        Optional<Prodotto> prodottoOptional = prodottoRepository.findById(id);
        if (prodottoOptional.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        if (elemento.getIdMagazzino() == null) {
            throw new RuntimeException(
                    "il magazzino non può essere null");
        }
        Optional<Magazzino> magazzinoTrovato = magazzinoRepository.findById(elemento.getIdMagazzino());
        if (magazzinoTrovato.isEmpty()) {
            throw new RuntimeException(
                    "l'id del magazzino non esiste, correggere.");
        }
        if (elemento.getPh() < 0 || elemento.getPh() > 14) {
            throw new RuntimeException(
                    "il pH ha valori compresi fra 0 e 14 se inizializzato.");
        }
        Prodotto prodotto = prodottoOptional.get();
        prodotto.setMagazzino(magazzinoTrovato.get());
        prodotto.setNome(elemento.getNome());
        prodotto.setDescrizione(elemento.getDescrizione());
        prodotto.setPh(elemento.getPh());
        prodotto = prodottoRepository.save(prodotto);

        return fromModelToDto(prodotto);

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
}
