package edu.esi.ds.esientradas.dao;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.esi.ds.esientradas.model.Espectaculo;

public interface EspectaculoDAO extends JpaRepository<Espectaculo, Long> {

    List<Espectaculo> findByEscenarioId(Long idEscenario);
    
    @Query("SELECT e FROM Espectaculo e WHERE DATE(e.fecha) = :fecha")
    List<Espectaculo> findByFecha(@Param("fecha") LocalDate fecha);

    List<Espectaculo> findByArtistaContaining(String artista);
}
