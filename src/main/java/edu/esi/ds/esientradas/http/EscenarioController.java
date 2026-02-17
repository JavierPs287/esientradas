package edu.esi.ds.esientradas.http;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import edu.esi.ds.esientradas.model.Escenario;

import edu.esi.ds.esientradas.services.EscenarioService;

@RestController
@RequestMapping("/escenarios")
public class EscenarioController {

    @Autowired
    private EscenarioService service;

    @PostMapping("/insertar")
    public void insertarEscenario(@RequestBody Escenario escenario) {
        this.service.insertarEscenario(escenario);
    }
}
