package edu.esi.ds.esientradas.http;

import java.util.List;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import edu.esi.ds.esientradas.dto.DtoEspectaculo;
import edu.esi.ds.esientradas.dto.DtoEntrada;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.model.Espectaculo;

import edu.esi.ds.esientradas.services.BusquedaService;

@RestController
@RequestMapping("/busqueda")
public class BusquedaController {

    @Autowired
    private BusquedaService service;

    @GetMapping("/getEscenarios")
    public List<Escenario> getEscenarios() {
        return this.service.getEscenarios();
    }

    @GetMapping("/getEspectaculos")
    public List<DtoEspectaculo> getEspectaculos(@RequestParam String artista) {
        List<Espectaculo> espectaculos = this.service.getEspectaculos(artista);
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

    //Con id escenario
    @GetMapping("/getEspectaculos/{idEscenario}")
    public List<DtoEspectaculo> getEspectaculos(@PathVariable Long idEscenario) {
        List<Espectaculo> espectaculos = this.service.getEspectaculos(idEscenario);
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

    @GetMapping("/getEntradas")
    public List<Entrada> getEntradas(@RequestParam Long espectaculoId) {
        return this.service.getEntradas(espectaculoId);
    }

    @GetMapping("/getMisEntradas")
    public List<DtoEntrada> getMisEntradas(@RequestParam Long userId) {
        List<Entrada> entradas = this.service.getMisEntradas(userId);
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

    @GetMapping("/saludar/{nombre}")
    public String saludar(@PathVariable String nombre, @RequestParam String apellido) {
        return "Hola " + nombre + " " + apellido + ", esta es la búsqueda de entradas.";
    }

    @GetMapping("/getEntradasLibres")
    public List<Entrada> getEntradasLibres(@RequestParam Long espectaculoId) {
        return this.service.getEntradasLibres(espectaculoId);
    }

    @GetMapping("/getEspectaculosPorFecha")
    public List<DtoEspectaculo> getEspectaculosPorFecha(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<Espectaculo> espectaculos = this.service.getEspectaculosPorFecha(fecha);
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
