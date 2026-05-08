package edu.esi.ds.esientradas.http;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.esi.ds.esientradas.services.ReservasService;

@RestController
@RequestMapping("/reservas")
public class ReservasController {

    @Autowired
    private ReservasService reservasService;

    @PutMapping("/reservar")
    public Long reservar(@RequestParam Long idEntrada, @RequestParam String token){
        return this.reservasService.reservar(idEntrada, token);
    }

    @PutMapping("/desreservar")
    public Long desreservar(@RequestParam Long idEntrada, @RequestParam String token){
        return this.reservasService.desreservar(idEntrada, token);
    }

    @GetMapping("/precioTotal")
    public Long getPrecioTotal(@RequestParam String token) {
        return this.reservasService.getTotalToken(token);
    }

    @GetMapping("/mis-entradas")
    public List<Long> getMisEntradas(@RequestParam String token) {
        return this.reservasService.getEntradasReservadas(token);
    }

    @PutMapping("/migrar")
    public void migrarReservas(@RequestParam String tokenAnonimo, @RequestParam String tokenAutenticado) {
        this.reservasService.migrarReservas(tokenAnonimo, tokenAutenticado);
    }

}
