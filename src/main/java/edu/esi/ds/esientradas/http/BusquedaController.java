package edu.esi.ds.esientradas.http;

import java.util.List;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import edu.esi.ds.esientradas.dto.DtoEspectaculo;
import edu.esi.ds.esientradas.dto.DtoEntrada;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Escenario;

import edu.esi.ds.esientradas.services.BusquedaService;
import edu.esi.ds.esientradas.services.UsuarioService;

@RestController
@RequestMapping("/busqueda")
public class BusquedaController {

    @Autowired
    private BusquedaService service;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/getEscenarios")
    public List<Escenario> getEscenarios() {
        return this.service.getEscenarios();
    }

    @GetMapping("/getEspectaculos")
    public List<DtoEspectaculo> getEspectaculos(@RequestParam String artista) {
        return this.service.getEspectaculos(artista);
    }

    //Con id escenario
    @GetMapping("/getEspectaculos/{idEscenario}")
    public List<DtoEspectaculo> getEspectaculos(@PathVariable Long idEscenario) {
        return this.service.getEspectaculos(idEscenario);
    }

    @GetMapping("/getEntradas")
    public List<Entrada> getEntradas(@RequestParam Long espectaculoId) {
        return this.service.getEntradas(espectaculoId);
    }

    @GetMapping("/getMisEntradas")
    public List<DtoEntrada> getMisEntradas(@RequestParam Long userId, @RequestParam String token) {
        this.usuarioService.validateUserAccess(token, userId);
        return this.service.getMisEntradas(userId);
    }

    // @GetMapping("/saludar/{nombre}")
    // public String saludar(@PathVariable String nombre, @RequestParam String apellido) {
    //     return "Hola " + nombre + " " + apellido + ", esta es la búsqueda de entradas.";
    // }

    @GetMapping("/getEntradasLibres")
    public List<Entrada> getEntradasLibres(@RequestParam Long espectaculoId) {
        return this.service.getEntradasLibres(espectaculoId);
    }

    @GetMapping("/getEspectaculosPorFecha")
    public List<DtoEspectaculo> getEspectaculosPorFecha(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return this.service.getEspectaculosPorFecha(fecha);
    }
}
