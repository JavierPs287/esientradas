package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import edu.esi.ds.esientradas.model.Escenario;

import edu.esi.ds.esientradas.dao.EscenarioDAO;
@Service
public class EscenarioService {

    @Autowired
    private EscenarioDAO dao;

    public void insertarEscenario(Escenario escenario) {

        if(escenario.getNombre() == null || escenario.getNombre().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del escenario no puede ser nulo o vacío");
        }
        if(escenario.getDescripcion() == null || escenario.getDescripcion().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripción del escenario no puede ser nula o vacía");
        }

        try{
            this.dao.save(escenario);
        }catch(DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al insertar el escenario: " + e.getMessage(), e);
        }
    }
}
