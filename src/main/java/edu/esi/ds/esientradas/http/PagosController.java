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
        return pagosService.prepararPago(infoPago);
    }

    @PostMapping("/confirmar")
    public String confirmarPago(@RequestBody(required = false) Map<String, Object> body) {
        return this.pagosService.confirmarPago(body);
    }

    @GetMapping("/getMisPagos")
    public List<DtoPagoHistorial> getMisPagos(@RequestParam Long userId, @RequestParam String token) {
        this.usuarioService.validateUserAccess(token, userId);
        return this.pagosService.getPagosPorUsuario(userId);
    }

}
