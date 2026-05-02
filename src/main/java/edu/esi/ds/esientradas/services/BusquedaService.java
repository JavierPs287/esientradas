package edu.esi.ds.esientradas.services;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.esi.ds.esientradas.model.Token;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.model.Estado;
import edu.esi.ds.esientradas.dao.EscenarioDAO;
import edu.esi.ds.esientradas.dao.EspectaculoDAO;
import edu.esi.ds.esientradas.dao.TokenDAO;
import edu.esi.ds.esientradas.dao.EntradaDAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Service
public class BusquedaService {

    @Autowired
    private EscenarioDAO escenarioDAO;

    @Autowired
    private EspectaculoDAO espectaculoDAO;
    
    @Autowired
    private EntradaDAO entradaDAO;

    @Autowired
    private TokenDAO tokenDAO;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Escenario> getEscenarios() {
        return this.escenarioDAO.findAll();
    }

    public List<Espectaculo> getEspectaculos(String artista) {
        return this.espectaculoDAO.findByArtista(artista);
    }

    public List<Espectaculo> getEspectaculos(Long idEscenario) {
       return this.espectaculoDAO.findByEscenarioId(idEscenario);
    }

    @Transactional
    public List<Entrada> getEntradas(Long espectaculoId) {
        updateEstado();
        entityManager.flush();  // Ejecucición delete y update
        entityManager.clear();  // Eliminación de cache para actualizar datos
        return this.entradaDAO.findByEspectaculoId(espectaculoId);
    }   
    
    @Transactional
    public List<Entrada> getMisEntradas(Long userId) {
        updateEstado();
        entityManager.flush();  // Ejecucición delete y update
        entityManager.clear();  // Eliminación de cache para actualizar datos
        return this.entradaDAO.findByUserId(userId);
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

    public List<Entrada> getEntradasLibres(Long espectaculoId) {
        return this.entradaDAO.findByEspectaculoIdAndEstado(espectaculoId, Estado.DISPONIBLE);
    }

    public List<Espectaculo> getEspectaculosPorFecha(LocalDate fecha) {
        return this.espectaculoDAO.findByFecha(fecha);
    }


}