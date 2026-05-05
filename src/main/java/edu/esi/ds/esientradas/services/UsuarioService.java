package edu.esi.ds.esientradas.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {

    private record ExternalSessionResponse(Long userId, String email) {}

    public String checkToken(String userToken) {

        String endpoint = "http://localhost:8081/external/checktoken";
        RestTemplate rest = new RestTemplate();

        try {
            String username = rest.getForObject(endpoint + "/" + userToken, String.class);

            if(username == null || username.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }
            return username;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed");
        }

        
    }

    public void validateUserAccess(String userToken, Long requestedUserId) {
        if (userToken == null || userToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Se necesita token");
        }
        if (requestedUserId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId es obligatorio");
        }

        String endpoint = "http://localhost:8081/external/session";
        RestTemplate rest = new RestTemplate();

        try {
            ExternalSessionResponse session = rest.getForObject(endpoint + "/" + userToken, ExternalSessionResponse.class);

            if (session == null || session.userId() == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido");
            }

            if (!requestedUserId.equals(session.userId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a las entradas de otro usuario");
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed");
        }
    }



}
