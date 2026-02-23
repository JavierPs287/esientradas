package edu.esi.ds.esientradas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.model.Espectaculo;

import edu.esi.ds.esientradas.dao.EscenarioDAO;
import edu.esi.ds.esientradas.dao.EspectaculoDAO;
import edu.esi.ds.esientradas.dao.EntradaDAO;


@Service
public class BusquedaService {

    @Autowired
    private EscenarioDAO escenarioDAO;

    @Autowired
    private EspectaculoDAO espectaculoDAO;
    
    @Autowired
    private EntradaDAO entradaDAO;

    public List<Escenario> getEscenarios() {
        return this.escenarioDAO.findAll();
    }

    public List<Espectaculo> getEspectaculos(String artista) {
        return this.espectaculoDAO.findByArtista(artista);
    }

    public List<Entrada> getEntradas(Long espectaculoId) {
        return this.entradaDAO.findByEspectaculoId(espectaculoId);
    }    
}
