package edu.esi.ds.esientradas.http;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.esi.ds.esientradas.dto.DtoPagoHistorial;
import edu.esi.ds.esientradas.services.PagosService;
import edu.esi.ds.esientradas.services.UsuarioService;

@RestController
@RequestMapping("/pagar")
public class PagosController {

    @Autowired
    PagosService pagosService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/prepararPago")
    public String prepararPago(@RequestBody Map<String, Object> infoPago) {
        if (!infoPago.containsKey("userId") || infoPago.get("userId") == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, 
                "userId es requerido");
        }

        if (!infoPago.containsKey("centimos") || infoPago.get("centimos") == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "centimos es requerido");
        }

        return pagosService.prepararPago(infoPago);
    }

    @PostMapping("/confirmar")
    public String confirmarPago(@RequestBody(required = false) Map<String, Object> body) {
        if (body == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "Body vacio");
        }

        if (!body.containsKey("token") || body.get("token") == null || String.valueOf(body.get("token")).isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "token es requerido");
        }

        if (!body.containsKey("userId") || body.get("userId") == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "userId es requerido");
        }

        String correoDestino = null;
        Object emailBody = body.get("email");
        if (emailBody instanceof String email && !email.isBlank()) {
            correoDestino = email;
        } else if (body.get("userEmail") instanceof String userEmail && !userEmail.isBlank()) {
            correoDestino = userEmail;
        }

        Long userId = ((Number) body.get("userId")).longValue();
        return this.pagosService.confirmarPago(String.valueOf(body.get("token")), correoDestino, userId);
    }

    @GetMapping("/getMisPagos")
    public List<DtoPagoHistorial> getMisPagos(@RequestParam Long userId, @RequestParam String token) {
        this.usuarioService.validateUserAccess(token, userId);
        return this.pagosService.getPagosPorUsuario(userId);
    }

}
