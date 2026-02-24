package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.esi.ds.esientradas.model.Token;

import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Long reservar(Long idEntrada) {
        Entrada entrada = this.entradaDAO.findById(idEntrada).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Entrada no encontrada")
        );
            
        if(entrada.getEstado() != Estado.DISPONIBLE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Entrada no disponible");
        }

        Token token = new Token();
        token.setEntrada(entrada);
        this.tokenDAO.save(token);

        this.entradaDAO.updateEstado(idEntrada, Estado.RESERVADA);
        return entrada.getPrecio();
    }
}
