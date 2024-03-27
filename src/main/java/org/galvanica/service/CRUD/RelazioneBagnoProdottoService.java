package org.galvanica.service.CRUD;

import org.galvanica.dto.dtoConModel.RelazioneBagnoProdottoDto;
import org.galvanica.model.Bagno;
import org.galvanica.model.Prodotto;
import org.galvanica.model.RelazioneBagnoProdotto;
import org.galvanica.repository.BagnoRepository;
import org.galvanica.repository.ProdottoRepository;
import org.galvanica.repository.RelazioneBagnoProdottoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RelazioneBagnoProdottoService implements ICRUDService<RelazioneBagnoProdottoDto, RelazioneBagnoProdotto> {

    private final RelazioneBagnoProdottoRepository relazioneBagnoProdottoRepository;
    private final BagnoRepository bagnoRepository;
    private final ProdottoRepository prodottoRepository;

    public RelazioneBagnoProdottoService(
            RelazioneBagnoProdottoRepository relazioneBagnoProdottoRepository,
            BagnoRepository bagnoRepository, ProdottoRepository prodottoRepository) {
        this.relazioneBagnoProdottoRepository = relazioneBagnoProdottoRepository;
        this.bagnoRepository = bagnoRepository;
        this.prodottoRepository = prodottoRepository;
    }

    @Override
    public RelazioneBagnoProdottoDto inserisci(RelazioneBagnoProdottoDto elemento) {
        if (elemento.getIdRelazione() != null) {
            throw new RuntimeException(
                    "l'id relazione NON deve essere valorizzato, è autoincrementale.");
        }
        if (elemento.getIdProdotto() == null) {
            throw new RuntimeException(
                    "l'id prodotto deve essere valorizzato.");
        }
        if (elemento.getIdBagno() == null) {
            throw new RuntimeException(
                    "l'id bagno deve essere valorizzato.");
        }
        Optional<Prodotto> prodottoOptional = prodottoRepository.findById(elemento.getIdProdotto());
        if (prodottoOptional.isEmpty()) {
            throw new RuntimeException(
                    "l'id del prodotto non esiste, correggere.");
        }
        Optional<Bagno> bagnoOptional = bagnoRepository.findById(elemento.getIdBagno());
        if (bagnoOptional.isEmpty()) {
            throw new RuntimeException(
                    "l'id del bagno non esiste, correggere.");
        }
        RelazioneBagnoProdotto relazioneBagnoProdotto = RelazioneBagnoProdotto.builder()
                .bagno(bagnoOptional.get())
                .prodotto(prodottoOptional.get())
                .note(elemento.getNote())
                .build();
        relazioneBagnoProdotto = relazioneBagnoProdottoRepository.save(
                relazioneBagnoProdotto);
        return fromModelToDto(relazioneBagnoProdotto);
    }

    @Override
    public Boolean elimina(long id) {
        boolean risposta = relazioneBagnoProdottoRepository.findById(id).isPresent();
        if (risposta) {
            relazioneBagnoProdottoRepository.deleteById(id);
        }
        return risposta;
    }

    @Override
    public RelazioneBagnoProdottoDto aggiorna(RelazioneBagnoProdottoDto elemento,
                                              long id) {
        Optional<RelazioneBagnoProdotto> relazioneBagnoProdottoOptional = relazioneBagnoProdottoRepository.findById(
                id);
        if (relazioneBagnoProdottoOptional.isEmpty()) {
            throw new RuntimeException(
                    "mettere un id corretto");
        }
        if (elemento.getIdProdotto() == null) {
            throw new RuntimeException(
                    "l'id prodotto deve essere valorizzato.");
        }
        if (elemento.getIdBagno() == null) {
            throw new RuntimeException(
                    "l'id bagno deve essere valorizzato.");
        }
        Optional<Prodotto> prodottoOptional = prodottoRepository.findById(elemento.getIdProdotto());
        if (prodottoOptional.isEmpty()) {
            throw new RuntimeException(
                    "l'id del prodotto non esiste, correggere.");
        }
        Optional<Bagno> bagnoOptional = bagnoRepository.findById(elemento.getIdBagno());
        if (bagnoOptional.isEmpty()) {
            throw new RuntimeException(
                    "l'id del bagno non esiste, correggere.");
        }
        RelazioneBagnoProdotto relazioneBagnoProdotto = relazioneBagnoProdottoOptional.get();
        relazioneBagnoProdotto.setNote(elemento.getNote());
        relazioneBagnoProdotto.setBagno(bagnoOptional.get());
        relazioneBagnoProdotto.setProdotto(prodottoOptional.get());
        relazioneBagnoProdotto = relazioneBagnoProdottoRepository.save(
                relazioneBagnoProdotto);
        return fromModelToDto(relazioneBagnoProdotto);
    }

    @Override
    public Optional<RelazioneBagnoProdottoDto> ricercaId(long id) {
        Optional<RelazioneBagnoProdotto> relazioneBagnoProdottoOptional =
                relazioneBagnoProdottoRepository.findById(id);

        return relazioneBagnoProdottoOptional
                .map(this::fromModelToDto);
    }

    @Override
    public RelazioneBagnoProdottoDto fromModelToDto(
            RelazioneBagnoProdotto oggettoDaTrasformare) {

        return RelazioneBagnoProdottoDto.builder()
                .idRelazione(oggettoDaTrasformare.getIdRelazione())
                .idBagno(oggettoDaTrasformare.getBagno().getIdBagno())
                .idProdotto(oggettoDaTrasformare.getProdotto().getIdProdotto())
                .note(oggettoDaTrasformare.getNote())
                .build();
    }
}
