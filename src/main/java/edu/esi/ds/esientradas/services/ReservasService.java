package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import edu.esi.ds.esientradas.dao.EntradaDAO;

import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;

@Service
public class ReservasService {

    @Autowired
    private EntradaDAO entradaDAO;

    @Transactional
    public Long reservar(Long idEntrada) {
        Entrada entrada = this.entradaDAO.findById(idEntrada).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Entrada no encontrada")
        );
            
        if(entrada.getEstado() != Estado.DISPONIBLE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Entrada no disponible");
        }

        this.entradaDAO.updateEstado(idEntrada, Estado.RESERVADA);
        return entrada.getPrecio();
    }
}
