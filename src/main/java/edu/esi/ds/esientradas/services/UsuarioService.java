package edu.esi.ds.esientradas.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private record ExternalSessionResponse(Long userId, String email) {}

    public String checkToken(String userToken) {

        String endpoint = "http://localhost:8081/external/checktoken";
        RestTemplate rest = new RestTemplate();

        try {
            String username = rest.getForObject(endpoint + "/" + userToken, String.class);

            if(username == null || username.isEmpty()) {
                logger.error("Token inválido: no se pudo obtener el nombre de usuario");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }

            logger.info("Token válido para usuario: {}", username);
            return username;
        } catch (RestClientException e) {
            logger.error("Error al validar el token");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed");
        }

    }

    public void validateUserAccess(String userToken, Long requestedUserId) {
        if (userToken == null || userToken.isBlank()) {
            logger.error("Token de usuario no proporcionado");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Se necesita token");
        }
        if (requestedUserId == null) {
            logger.error("ID de usuario solicitado es nulo");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId es obligatorio");
        }

        String endpoint = "http://localhost:8081/external/session";
        RestTemplate rest = new RestTemplate();

        try {
            ExternalSessionResponse session = rest.getForObject(endpoint + "/" + userToken, ExternalSessionResponse.class);

            if (session == null || session.userId() == null) {
                logger.error("Token inválido o usuario no encontrado");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido");
            }

            if (!requestedUserId.equals(session.userId())) {
                logger.error("Acceso denegado: usuario {} intentando acceder a datos de usuario {}", session.userId(), requestedUserId);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a las entradas de otro usuario");
            }
        } catch (ResponseStatusException e) {
            logger.error("Error de validación de token: {}", e.getReason());
            throw e;
        } catch (RestClientException e) {
            logger.error("Error al validar el token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed");
        }
    }

}
