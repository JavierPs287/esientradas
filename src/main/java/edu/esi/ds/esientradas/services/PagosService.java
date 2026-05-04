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

@Service
public class PagosService {

    // @Autowired
    // ConfiguracionDAO configuracionDAO;

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

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;


    public String prepararPago(Map<String,Object> infoPago) {
        try{
            Stripe.apiKey = stripeSecretKey.trim();
            Long centimos = ((Number) infoPago.get("centimos")).longValue();
            Long idReserva = ((Number) infoPago.get("idReserva")).longValue();

            PaymentIntentCreateParams params = 
                PaymentIntentCreateParams.builder()
                    .setAmount(centimos)
                    .setCurrency("eur")
                    .putMetadata("idReserva", idReserva.toString())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            return paymentIntent.getClientSecret();

        } catch (Exception e) {
            System.err.println("Stripe error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error al preparar el pago: " + e.getMessage());
        }
    }


    @Transactional
    public String confirmarPago(String sessionId, String correoDestino, Long userId) {
        List<Token> tokens = tokenDAO.findAllBySessionId(sessionId);
        for (Token token : tokens) {
            Entrada entrada = token.getEntrada();

            // ✅ Solo dirty checking, sin bulk UPDATE
            entrada.setUserId(userId);
            entrada.setEstado(Estado.VENDIDA);  // ← Hibernate hace el UPDATE al final del método
            entradaDAO.save(entrada);           // ← fuerza flush inmediato de esta entidad

            Pago pago = new Pago();
            pago.setFechaPago(LocalDateTime.now());
            pago.setCosto(entrada.getPrecio());
            pago.setEntrada(entrada);
            pago.setIdUsuario(userId);

            pagoDAO.save(pago);
            tokenDAO.delete(token);

            pdfService.generarPdfEntrada(entrada);
            if (correoDestino != null && !correoDestino.isBlank()) {
                gmailEmailService.sendPDF(correoDestino, "Tu entrada en PDF", entrada.getId());
            }
        }
        return "ok";
    }

    @Transactional(readOnly = true)
    public List<DtoPagoHistorial> getPagosPorUsuario(Long userId) {
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
