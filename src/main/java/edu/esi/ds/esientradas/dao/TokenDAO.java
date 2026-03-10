package edu.esi.ds.esientradas.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import edu.esi.ds.esientradas.model.Token;

public interface TokenDAO extends JpaRepository<Token, String> {

    boolean existsBySessionIdAndEntradaId(String sessionId, Long idEntrada);

    Optional<Token> findBySessionIdAndEntradaId(String sessionId, Long idEntrada);

    List<Token> findAllBySessionId(String sessionId);

}
