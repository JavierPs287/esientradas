package edu.esi.ds.esientradas.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.esi.ds.esientradas.model.Escenario;

public interface EscenarioDAO extends JpaRepository<Escenario, Long> {

}
