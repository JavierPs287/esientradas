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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class BusquedaService {

    private static final Logger logger = LoggerFactory.getLogger(BusquedaService.class);

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
        logger.info("Obteniendo todos los escenarios");
        return this.escenarioDAO.findAll();
    }

    public List<DtoEspectaculo> getEspectaculos(String artista) {
        logger.info("Buscando espectáculos por artista: {}", artista);
        List<Espectaculo> espectaculos = this.espectaculoDAO.findByArtistaContaining(artista);
        logger.info("Encontrados {} espectáculos para el artista '{}'", espectaculos.size(), artista);
        List<DtoEspectaculo> dtos = espectaculos.stream().map(e -> {
            DtoEspectaculo dto = new DtoEspectaculo();
            dto.setId(e.getId());
            dto.setArtista(e.getArtista());
            dto.setFecha(e.getFecha());
            dto.setEscenario(e.getEscenario().getNombre());
            logger.info("Espectáculo encontrado: ID={}, Artista={}, Fecha={}, Escenario={}", 
                dto.getId(), dto.getArtista(), dto.getFecha(), dto.getEscenario());
            return dto;
        }).toList();
        return dtos;
    }

    public List<DtoEspectaculo> getEspectaculos(Long idEscenario) {
        logger.info("Buscando espectáculos por escenario ID: {}", idEscenario);
        List<Espectaculo> espectaculos = this.espectaculoDAO.findByEscenarioId(idEscenario);
        logger.info("Encontrados {} espectáculos para el escenario ID '{}'", espectaculos.size(), idEscenario);
        List<DtoEspectaculo> dtos = espectaculos.stream().map(e -> {
            DtoEspectaculo dto = new DtoEspectaculo();
            dto.setId(e.getId());
            dto.setArtista(e.getArtista());
            dto.setFecha(e.getFecha());
            dto.setEscenario(e.getEscenario().getNombre());
            logger.info("Espectáculo encontrado: ID={}, Artista={}, Fecha={}, Escenario={}", 
                dto.getId(), dto.getArtista(), dto.getFecha(), dto.getEscenario());
            return dto;
        }).toList();
        return dtos;
    }

    @Transactional
    public List<Entrada> getEntradas(Long espectaculoId) {
        updateEstado();
        entityManager.flush();  // Ejecucición delete y update
        entityManager.clear();  // Eliminación de cache para actualizar datos
        logger.info("Obteniendo entradas para el espectáculo ID: {}", espectaculoId);
        return this.entradaDAO.findByEspectaculoId(espectaculoId);
    }   
    
    @Transactional
    public List<DtoEntrada> getMisEntradas(Long userId) {
        updateEstado();
        entityManager.flush();  // Ejecucición delete y update
        entityManager.clear();  // Eliminación de cache para actualizar datos
        logger.info("Obteniendo mis entradas para el usuario ID: {}", userId);
        List<Entrada> entradas = this.entradaDAO.findByUserId(userId);
        logger.info("Encontradas {} entradas para el usuario ID '{}'", entradas.size(), userId);

        List<DtoEntrada> dtos = entradas.stream().map(entrada -> {
            DtoEntrada dto = new DtoEntrada();
            dto.setId(entrada.getId());
            dto.setPrecio(entrada.getPrecio());
            dto.setArtista(entrada.getEspectaculo().getArtista());
            dto.setFecha(entrada.getEspectaculo().getFecha());
            dto.setEscenarioNombre(entrada.getEspectaculo().getEscenario().getNombre());
            logger.info("Entrada encontrada: ID={}, Precio={}, Artista={}, Fecha={}, Escenario={}", 
                dto.getId(), dto.getPrecio(), dto.getArtista(), dto.getFecha(), dto.getEscenarioNombre());
            return dto;
        }).toList();
        return dtos;
    }

    //TODO Unificar
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateEstado(){
        List<Token> tokens = this.tokenDAO.findAll();
        for (Token token : tokens) {
            if(token.getExpiracion().isBefore(Instant.now())){
                this.entradaDAO.updateEstado(token.getEntrada().getId(), Estado.DISPONIBLE);
                logger.info("Token expirado encontrado: ID={}, EntradaID={}, Expiración={}", 
                    token.getValor(), token.getEntrada().getId(), token.getExpiracion());
                logger.info("Actualizando estado de la entrada ID {} a DISPONIBLE", token.getEntrada().getId());
                this.tokenDAO.delete(token);
            }
        }
    }

    public List<Entrada> getEntradasLibres(Long espectaculoId) {
        logger.info("Obteniendo entradas libres para el espectáculo ID: {}", espectaculoId);
        return this.entradaDAO.findByEspectaculoIdAndEstado(espectaculoId, Estado.DISPONIBLE);
    }

    public List<DtoEspectaculo> getEspectaculosPorFecha(LocalDate fecha) {
        logger.info("Buscando espectáculos por fecha: {}", fecha);
        List<Espectaculo> espectaculos = this.espectaculoDAO.findByFecha(fecha);
        logger.info("Encontrados {} espectáculos para la fecha '{}'", espectaculos.size(), fecha);
        List<DtoEspectaculo> dtos = espectaculos.stream().map(e -> {
            DtoEspectaculo dto = new DtoEspectaculo();
            dto.setId(e.getId());
            dto.setArtista(e.getArtista());
            dto.setFecha(e.getFecha());
            dto.setEscenario(e.getEscenario().getNombre());
            logger.info("Espectáculo encontrado: ID={}, Artista={}, Fecha={}, Escenario={}", 
                dto.getId(), dto.getArtista(), dto.getFecha(), dto.getEscenario());
            return dto;
        }).toList();
        return dtos;
    }


}