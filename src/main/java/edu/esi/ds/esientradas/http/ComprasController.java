package edu.esi.ds.esientradas.http;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import edu.esi.ds.esientradas.services.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/compras")
public class ComprasController {

    @Autowired
    private UsuarioService usuarioService;

    @PutMapping("/comprar")
    public String comprar(HttpServletResponse response, @RequestParam String userToken) throws IOException{
        if(userToken == null || userToken.isBlank()){
            response.sendRedirect("https://www.uclm.es/");
            return "";
        }

        return this.usuarioService.checkToken(userToken);
        
    }

}
