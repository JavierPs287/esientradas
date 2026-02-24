package edu.esi.ds.esientradas.services;

import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
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
    public Long reservar(Long idEntrada, HttpSession session) {
        updateEstado();
        entityManager.flush();  // Ejecucición delete y update
        entityManager.clear();  // Eliminación de cache para actualizar datos
        Entrada entrada = this.entradaDAO.findById(idEntrada).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Entrada no encontrada")
        );
            
        if(entrada.getEstado() != Estado.DISPONIBLE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Entrada no disponible");
        }

        Token token = new Token();
        token.setEntrada(entrada);
        token.setSessionId(session.getId());
        this.tokenDAO.save(token);

        this.entradaDAO.updateEstado(idEntrada, Estado.RESERVADA);
        return entrada.getPrecio();
    }

    //TODO Unificar
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateEstado(){
        List<Token> tokens = this.tokenDAO.findAll();
        for (Token token : tokens) {
            if(token.getExpiracion().isBefore(Instant.now())){
                this.entradaDAO.updateEstado(token.getEntrada().getId(), Estado.DISPONIBLE);
                this.tokenDAO.delete(token);
            }
        }
    }
}
