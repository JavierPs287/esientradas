package edu.esi.ds.esientradas.http;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    public Long reservar(HttpSession session, @RequestParam Long idEntrada){
        return this.reservasService.reservar(idEntrada, session);
    }

    @PutMapping("/desreservar")
    public Long desreservar(HttpSession session, @RequestParam Long idEntrada){
        return this.reservasService.desreservar(idEntrada, session);
    }

    @GetMapping("/precioTotal")
    public Long getPrecioTotal(HttpSession session) {
        return (Long) session.getAttribute("precioTotal");
    }

}
