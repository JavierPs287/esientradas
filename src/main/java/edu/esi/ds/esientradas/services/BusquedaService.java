package edu.esi.ds.esientradas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.model.Espectaculo;

import edu.esi.ds.esientradas.dao.EscenarioDAO;
import edu.esi.ds.esientradas.dao.EspectaculoDAO;

import org.springframework.stereotype.Service;

@Service
public class BusquedaService {

    @Autowired
    private EscenarioDAO escenariodao;

    @Autowired
    private EspectaculoDAO espectaculodao;

    public List<Escenario> getEscenarios() {
        return this.escenariodao.findAll();
    }

    public List<Espectaculo> getEspectaculos(String artista) {
        return this.espectaculodao.findByArtista(artista);
    }
}
