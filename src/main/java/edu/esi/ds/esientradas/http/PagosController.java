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
public class PagosController {

    @Autowired
    PagosService pagosService;

    @PostMapping("/prepararPago")
    public String prepararPago(@RequestBody Map<String, Object> infoPago, HttpSession session) {
        // Validar que el userId viene en la request
        if (!infoPago.containsKey("userId") || infoPago.get("userId") == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, 
                "userId es requerido");
        }
        
        infoPago.put("centimos",session.getAttribute("precioTotal"));
        return pagosService.prepararPago(infoPago);
    }

    @PostMapping("/confirmar")
    public String confirmarPago(@RequestBody(required = false) Map<String, Object> body, HttpSession session) {
        String correoDestino = (String) session.getAttribute("correoUsuario");
        Long userId = null;

        if (body != null) {
            // Validar que el userId viene en la request
            if (!body.containsKey("userId") || body.get("userId") == null) {
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, 
                    "userId es requerido");
            }
            userId = ((Number) body.get("userId")).longValue();
            
            Object emailBody = body.get("email");
            if (emailBody instanceof String email && !email.isBlank()) {
                correoDestino = email;
                session.setAttribute("correoUsuario", email);
            }
        }

        session.setAttribute("precioTotal", 0L);
        return this.pagosService.confirmarPago(session.getId(), correoDestino, userId);
    }

}
