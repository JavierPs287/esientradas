package edu.esi.ds.esientradas.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import edu.esi.ds.esientradas.model.Token;
import edu.esi.ds.esientradas.dao.EntradaDAO;
import edu.esi.ds.esientradas.dao.TokenDAO;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;

@Service
public class ReservasService {

    @Autowired
    private EntradaDAO entradaDAO;

    @Autowired
    private TokenDAO tokenDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Long reservar(Long idEntrada, String tokenUsuario) {
        updateEstado();

        Entrada entrada = this.entradaDAO.findById(idEntrada).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no encontrada"));

        // Comprobamos si la entrada ya la habia reservado el mismo user
        if (this.tokenDAO.existsByTokenUsuarioAndEntradaId(tokenUsuario, idEntrada)) {
            return getTotalToken(tokenUsuario);
        }

        if (entrada.getEstado() != Estado.DISPONIBLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entrada no disponible");
        }

        Token token = new Token();
        token.setEntrada(entrada);
        token.setTokenUsuario(tokenUsuario);
        this.tokenDAO.save(token);

        this.entradaDAO.updateEstado(idEntrada, Estado.RESERVADA);
        return getTotalToken(tokenUsuario) + entrada.getPrecio();
    }

    @Transactional
    public Long desreservar(Long idEntrada, String tokenUsuario) {
        updateEstado();

        Optional<Token> tokenOpt = tokenDAO.findByTokenUsuarioAndEntradaId(tokenUsuario, idEntrada);

        // Si no existe el token, la entrada no esta reservada por lo que no se desreserva (devolvemos el precio total actual)
        if (tokenOpt.isEmpty()) {
            return getTotalToken(tokenUsuario);
        }

        Token token = tokenOpt.get();
        long precioEntrada = token.getEntrada().getPrecio();
        this.tokenDAO.delete(token);
        this.entradaDAO.updateEstado(idEntrada, Estado.DISPONIBLE);

        long total = Math.max(0L, getTotalToken(tokenUsuario) - precioEntrada);
        return total;
    }

    @Transactional(readOnly = true)
    public Long getTotalToken(String tokenUsuario) {
        return this.tokenDAO.findAllByTokenUsuario(tokenUsuario).stream()
                .mapToLong(token -> token.getEntrada().getPrecio())
                .sum();
    }

    // TODO Unificar
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateEstado() {
        List<Token> tokens = this.tokenDAO.findAll();
        for (Token token : tokens) {
            if (token.getExpiracion().isBefore(Instant.now())) {
                this.entradaDAO.updateEstado(token.getEntrada().getId(), Estado.DISPONIBLE);
                this.tokenDAO.delete(token);
            }
        }
        entityManager.flush(); // Ejecucición delete y update
        entityManager.clear(); // Eliminación de cache para actualizar datos
    }

}
