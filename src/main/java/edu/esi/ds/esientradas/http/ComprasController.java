package edu.esi.ds.esientradas.http;

import java.io.IOException;
import java.util.Map;

import org.json.HTTP;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.esi.ds.esientradas.services.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/compras")
public class ComprasController {

    private UsuarioService usuarioService;

    @PutMapping("/comprar")
    public void comprar(HttpSession session,HttpServletResponse response, @RequestParam String userToken) throws IOException{
        String sessionId = session.getId();
        if(sessionId == null || sessionId.isEmpty()){
            response.sendRedirect("https://www.uclm.es/");
            return;
        }

        this.usuarioService.checkToken(userToken);
        
    }

}
