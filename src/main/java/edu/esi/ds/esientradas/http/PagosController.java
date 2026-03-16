package edu.esi.ds.esientradas.http;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.esi.ds.esientradas.services.PagosService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/pagar")
@CrossOrigin(origins = "http://localhost:4200",
allowCredentials = "true"
)
public class PagosController {

    @Autowired
    PagosService pagosService;

    @PostMapping("/prepararPago")
    public String prepararPago(@RequestBody Map<String, Object> infoPago, HttpSession session) {
        infoPago.put("centimos",session.getAttribute("precioTotal"));
        return pagosService.prepararPago(infoPago);
    }

    @PostMapping("/confirmar")
    public String confirmarPago(HttpSession session) {
        session.setAttribute("precioTotal", 0L);
        return this.pagosService.confirmarPago(session.getId());
    }

}
