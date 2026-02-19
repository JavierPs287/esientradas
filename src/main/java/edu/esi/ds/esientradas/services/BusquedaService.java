package edu.esi.ds.esientradas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.model.Espectaculo;
<<<<<<< HEAD

=======
>>>>>>> ea08172e203610baa8a53b5883f56a300e91d28c
import edu.esi.ds.esientradas.dao.EscenarioDAO;
import edu.esi.ds.esientradas.dao.EspectaculoDAO;

import org.springframework.stereotype.Service;

@Service
public class BusquedaService {

    @Autowired
<<<<<<< HEAD
    private EscenarioDAO escenariodao;

    @Autowired
    private EspectaculoDAO espectaculodao;

    public List<Escenario> getEscenarios() {
        return this.escenariodao.findAll();
    }

    public List<Espectaculo> getEspectaculos(String artista) {
        return this.espectaculodao.findByArtista(artista);
=======
    private EscenarioDAO escenarioDAO;

    @Autowired
    private EspectaculoDAO espectaculoDAO;

    public List<Escenario> getEscenarios() {
        return this.escenarioDAO.findAll();
    }

    public List<Espectaculo> getEspectaculos(String artista) {
        return this.espectaculoDAO.findByArtista(artista);
>>>>>>> ea08172e203610baa8a53b5883f56a300e91d28c
    }
}
