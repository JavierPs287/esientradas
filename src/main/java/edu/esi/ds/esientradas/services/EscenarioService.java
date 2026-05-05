package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import edu.esi.ds.esientradas.model.Escenario;

import edu.esi.ds.esientradas.dao.EscenarioDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class EscenarioService {

    @Autowired
    private EscenarioDAO dao;

    private static final Logger logger = LoggerFactory.getLogger(EscenarioService.class);

    public void insertarEscenario(Escenario escenario) {

        if(escenario.getNombre() == null || escenario.getNombre().isEmpty()) {
            logger.error("El nombre del escenario no puede ser nulo o vacío");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del escenario no puede ser nulo o vacío");
        }
        if(escenario.getDescripcion() == null || escenario.getDescripcion().isEmpty()) {
            logger.error("La descripción del escenario no puede ser nula o vacía");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripción del escenario no puede ser nula o vacía");
        }

        try{
            logger.info("Insertando escenario: {}", escenario.getNombre());
            this.dao.save(escenario);
            logger.info("Escenario insertado correctamente: {}", escenario.getNombre());
        }catch(DataIntegrityViolationException e) {
            logger.error("Error de integridad de datos al insertar el escenario: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        
        } catch(Exception e) {
            logger.error("Error al insertar el escenario: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al insertar el escenario: " + e.getMessage(), e);
        }
    }
}
