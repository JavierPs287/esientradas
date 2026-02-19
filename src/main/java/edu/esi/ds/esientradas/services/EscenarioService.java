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

    public void insertar(Escenario escenario) {
        try {
            this.dao.save(escenario);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error inesperado al insertar el escenario: " + e.getMessage(), e);
        }
    }
}
