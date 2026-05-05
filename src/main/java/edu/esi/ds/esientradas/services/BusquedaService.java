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
import edu.esi.ds.esientradas.dto.DtoEntrada;
import edu.esi.ds.esientradas.dto.DtoEspectaculo;
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

    public List<DtoEspectaculo> getEspectaculos(String artista) {
        List<Espectaculo> espectaculos = this.espectaculoDAO.findByArtistaContaining(artista);
        List<DtoEspectaculo> dtos = espectaculos.stream().map(e -> {
            DtoEspectaculo dto = new DtoEspectaculo();
            dto.setId(e.getId());
            dto.setArtista(e.getArtista());
            dto.setFecha(e.getFecha());
            dto.setEscenario(e.getEscenario().getNombre());
            return dto;
        }).toList();
        return dtos;
    }

    public List<DtoEspectaculo> getEspectaculos(Long idEscenario) {
        List<Espectaculo> espectaculos = this.espectaculoDAO.findByEscenarioId(idEscenario);
        List<DtoEspectaculo> dtos = espectaculos.stream().map(e -> {
            DtoEspectaculo dto = new DtoEspectaculo();
            dto.setId(e.getId());
            dto.setArtista(e.getArtista());
            dto.setFecha(e.getFecha());
            dto.setEscenario(e.getEscenario().getNombre());
            return dto;
        }).toList();
        return dtos;
    }

    @Transactional
    public List<Entrada> getEntradas(Long espectaculoId) {
        updateEstado();
        entityManager.flush();  // Ejecucición delete y update
        entityManager.clear();  // Eliminación de cache para actualizar datos
        return this.entradaDAO.findByEspectaculoId(espectaculoId);
    }   
    
    @Transactional
    public List<DtoEntrada> getMisEntradas(Long userId) {
        updateEstado();
        entityManager.flush();  // Ejecucición delete y update
        entityManager.clear();  // Eliminación de cache para actualizar datos
        List<Entrada> entradas = this.entradaDAO.findByUserId(userId);

        return entradas.stream().map(entrada -> {
            return new DtoEntrada(
                entrada.getId(),
                entrada.getPrecio(),
                entrada.getEspectaculo().getArtista(),
                entrada.getEspectaculo().getFecha(),
                entrada.getEspectaculo().getEscenario().getNombre()
            );
        }).toList();
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

    public List<DtoEspectaculo> getEspectaculosPorFecha(LocalDate fecha) {
        List<Espectaculo> espectaculos = this.espectaculoDAO.findByFecha(fecha);
        List<DtoEspectaculo> dtos = espectaculos.stream().map(e -> {
            DtoEspectaculo dto = new DtoEspectaculo();
            dto.setId(e.getId());
            dto.setArtista(e.getArtista());
            dto.setFecha(e.getFecha());
            dto.setEscenario(e.getEscenario().getNombre());
            return dto;
        }).toList();
        return dtos;
    }


}