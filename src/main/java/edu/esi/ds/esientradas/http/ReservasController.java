package edu.esi.ds.esientradas.http;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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

    // TODO A lo mejor conviene cambiar coger el array de entradas en lugar de ir 1 por 1
    @PutMapping("/reservar")
    public Long reservar(HttpSession session, @RequestParam Long idEntrada){
        Long precioEntrada = this.reservasService.reservar(idEntrada);
        Long precioTotal = (Long) session.getAttribute("precioTotal");
        if(precioTotal == null){
            precioTotal = precioEntrada;
            session.setAttribute("precioTotal", precioTotal);
        } else {
            precioTotal += precioEntrada;
            session.setAttribute("precioTotal", precioTotal);
        }
        return precioTotal;
    }
}
