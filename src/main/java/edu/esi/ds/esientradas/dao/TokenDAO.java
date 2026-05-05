package edu.esi.ds.esientradas.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import edu.esi.ds.esientradas.model.Token;

public interface TokenDAO extends JpaRepository<Token, String> {

    boolean existsByTokenUsuarioAndEntradaId(String tokenUsuario, Long idEntrada);

    Optional<Token> findByTokenUsuarioAndEntradaId(String tokenUsuario, Long idEntrada);

    List<Token> findAllByTokenUsuario(String tokenUsuario);

}
