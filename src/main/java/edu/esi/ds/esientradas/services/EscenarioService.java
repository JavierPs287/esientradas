package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
<<<<<<< HEAD
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
=======
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
>>>>>>> ea08172e203610baa8a53b5883f56a300e91d28c

import edu.esi.ds.esientradas.model.Escenario;

import edu.esi.ds.esientradas.dao.EscenarioDAO;

<<<<<<< HEAD
=======
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

>>>>>>> ea08172e203610baa8a53b5883f56a300e91d28c
@Service
public class EscenarioService {

    @Autowired
    private EscenarioDAO dao;

<<<<<<< HEAD
    public void insertar(Escenario escenario) {
        try {
            this.dao.save(escenario);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error inesperado al insertar el escenario: " + e.getMessage(), e);
        }
=======
    public void insertarEscenario(Escenario escenario) {
        try{
            this.dao.save(escenario);
        }catch(DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al insertar el escenario: " + e.getMessage(), e);
        }

>>>>>>> ea08172e203610baa8a53b5883f56a300e91d28c
    }
}
