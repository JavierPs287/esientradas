package edu.esi.ds.esientradas.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.esi.ds.esientradas.dao.ConfiguracionDAO;

@Service
public class PagosService {

    @Autowired
    ConfiguracionDAO configuracionDAO;

    private static final String secretKey = "key";

    public String prepararPago(Map<String,Object> infoPago) {
        return null;
    }

    public String confirmarPago() {
        return null;
    }

}
