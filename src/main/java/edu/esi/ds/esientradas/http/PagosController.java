package edu.esi.ds.esientradas.http;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.esi.ds.esientradas.services.PagosService;

@RestController
@RequestMapping("/pagar")
@CrossOrigin(origins = "http://localhost:4200",
allowCredentials = "true"
)
public class PagosController {

    @Autowired
    PagosService pagosService;

    @PostMapping("/prepararPago")
    public void prepararPago(@RequestBody Map<String, Object> infoPago) {
        Long centimos = ((Number) infoPago.get("centimos")).longValue();
        this.pagosService.prepararPago(infoPago);
    }

    @PostMapping("/confirmar")
    public String confirmarPago() {
        return this.pagosService.confirmarPago();
    }

}
