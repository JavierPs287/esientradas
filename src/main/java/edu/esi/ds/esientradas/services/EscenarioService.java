package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Autowired;

import edu.esi.ds.esientradas.model.Escenario;

import edu.esi.ds.esientradas.dao.EscenarioDAO;

import org.springframework.stereotype.Service;

@Service
public class EscenarioService {

    @Autowired
    private EscenarioDAO dao;

    public void insertarEscenario(Escenario escenario) {
        this.dao.save(escenario);
    }
}
