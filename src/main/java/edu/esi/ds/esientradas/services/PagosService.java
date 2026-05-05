package edu.esi.ds.esientradas.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import edu.esi.ds.esientradas.dao.EntradaDAO;
import edu.esi.ds.esientradas.dao.PagoDAO;
import edu.esi.ds.esientradas.dao.TokenDAO;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;
import edu.esi.ds.esientradas.model.Pago;
import edu.esi.ds.esientradas.model.Token;
import edu.esi.ds.esientradas.dto.DtoPagoHistorial;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PagosService {

    // @Autowired
    // ConfiguracionDAO configuracionDAO;

    private static final Logger logger = LoggerFactory.getLogger(PagosService.class);

    @Autowired
    private EntradaDAO entradaDAO;

    @Autowired
    private TokenDAO tokenDAO;

    @Autowired
    private PagoDAO pagoDAO;

    @Autowired
    private PDFService pdfService;

    @Autowired
    private GmailEmailService gmailEmailService;

    @Autowired
    private UsuarioService usuarioService;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;


    public String prepararPago(Map<String,Object> infoPago) {

        if (!infoPago.containsKey("token") || infoPago.get("token") == null || String.valueOf(infoPago.get("token")).isBlank()) {
            logger.error("Falta token en la solicitud de prepararPago");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "token es requerido");
        }

        if (!infoPago.containsKey("userId") || infoPago.get("userId") == null) {
            logger.error("Falta userId en la solicitud de prepararPago");
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, 
                "userId es requerido");
        }

        if (!infoPago.containsKey("centimos") || infoPago.get("centimos") == null) {
            logger.error("Falta centimos en la solicitud de prepararPago");
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "centimos es requerido");
        }

        try{
            Stripe.apiKey = stripeSecretKey.trim();
            String token = String.valueOf(infoPago.get("token"));
            Long userId = ((Number) infoPago.get("userId")).longValue();
            Long centimos = ((Number) infoPago.get("centimos")).longValue();
            Long idReserva = ((Number) infoPago.get("idReserva")).longValue();

            usuarioService.validateUserAccess(token, userId);

            logger.info("Preparando pago para userId={}, centimos={}, idReserva={}", userId, centimos, idReserva);
            PaymentIntentCreateParams params = 
                PaymentIntentCreateParams.builder()
                    .setAmount(centimos)
                    .setCurrency("eur")
                    .putMetadata("idReserva", idReserva.toString())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            return paymentIntent.getClientSecret();

        } catch (Exception e) {
            logger.error("Error al preparar el pago: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error al preparar el pago: " + e.getMessage());
        }
    }


    @Transactional
    public String confirmarPago(Map<String, Object> body) {
        
        if (body == null) {
            logger.error("Body de la solicitud de confirmarPago es null");
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "Body vacio");
        }

        if (!body.containsKey("token") || body.get("token") == null || String.valueOf(body.get("token")).isBlank()) {
            logger.error("Falta token en la solicitud de confirmarPago");
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "token es requerido");
        }

        if (!body.containsKey("userId") || body.get("userId") == null) {
            logger.error("Falta userId en la solicitud de confirmarPago");
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "userId es requerido");
        }

        String correoDestino = null;
        Object emailBody = body.get("email");
        if (emailBody instanceof String email && !email.isBlank()) {
            logger.info("Correo destino proporcionado en la solicitud de confirmarPago: {}", email);
            correoDestino = email;
        } else if (body.get("userEmail") instanceof String userEmail && !userEmail.isBlank()) {
            logger.info("Correo destino proporcionado en userEmail en la solicitud de confirmarPago: {}", userEmail);
            correoDestino = userEmail;
        }

        Long userId = ((Number) body.get("userId")).longValue();

        String tokenUsuario = String.valueOf(body.get("token"));
        String emailSesion = usuarioService.checkToken(tokenUsuario);
        usuarioService.validateUserAccess(tokenUsuario, userId);

        if (correoDestino != null && !correoDestino.equalsIgnoreCase(emailSesion)) {
            logger.error("Correo de destino no corresponde al usuario autenticado para userId={}", userId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes usar un correo distinto al de tu cuenta");
        }

        if (correoDestino == null) {
            correoDestino = emailSesion;
        }

        List<Token> tokens = tokenDAO.findAllByTokenUsuario(tokenUsuario);
        for (Token token : tokens) {
            Entrada entrada = token.getEntrada();

            // ✅ Solo dirty checking, sin bulk UPDATE
            logger.info("Modificando estado de entrada id={} a VENDIDA para userId={}", entrada.getId(), userId);
            entrada.setUserId(userId);
            entrada.setEstado(Estado.VENDIDA);  // ← Hibernate hace el UPDATE al final del método
            entradaDAO.save(entrada);           // ← fuerza flush inmediato de esta entidad
            logger.info("Estado de entrada id={} modificado a VENDIDA", entrada.getId());

            Pago pago = new Pago();
            pago.setFechaPago(LocalDateTime.now());
            pago.setCosto(entrada.getPrecio());
            pago.setEntrada(entrada);
            pago.setIdUsuario(userId);

            logger.info("Guardando pago para entrada id={} y userId={}", entrada.getId(), userId);
            pagoDAO.save(pago);
            tokenDAO.delete(token);
            logger.info("Pago guardado y token eliminado para entrada id={} y userId={}", entrada.getId(), userId);

            logger.info("Generando PDF para entrada id={}", entrada.getId());
            pdfService.generarPdfEntrada(entrada);
            logger.info("PDF generado para entrada id={}", entrada.getId());
            if (correoDestino != null && !correoDestino.isBlank()) {
                gmailEmailService.sendPDF(correoDestino, "Tu entrada en PDF", entrada.getId());
            }
        }
        logger.info("Pago confirmado para userId={}, total entradas pagadas={}", userId, tokens.size());
        return "ok";
    }

    @Transactional(readOnly = true)
    public List<DtoPagoHistorial> getPagosPorUsuario(Long userId) {
        logger.info("Obteniendo historial de pagos para userId={}", userId);
        return pagoDAO.findByIdUsuarioOrderByFechaPagoDesc(userId).stream()
                .map(pago -> {
                    Entrada entrada = pago.getEntrada();
                    return new DtoPagoHistorial(
                            pago.getId(),
                            pago.getCosto(),
                            pago.getFechaPago(),
                            entrada.getId(),
                            entrada.getEspectaculo().getArtista(),
                            entrada.getEspectaculo().getFecha(),
                            entrada.getEspectaculo().getEscenario().getNombre());
                })
                .collect(Collectors.toList());
    }

}
