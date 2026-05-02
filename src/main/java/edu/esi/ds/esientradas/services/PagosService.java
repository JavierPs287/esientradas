package edu.esi.ds.esientradas.services;

import java.util.List;
import java.util.Map;

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
            Pago pago = new Pago();
            // Guardar userId en la entrada
            entrada.setUserId(userId);
            entradaDAO.updateEstado(entrada.getId(), Estado.VENDIDA);
            entrada.setEstado(Estado.VENDIDA);
            pago.setCosto(entrada.getPrecio());
            pago.setEntrada(entrada);
            pago.setIdUsuario(userId);
            pdfService.generarPdfEntrada(entrada);
            if (correoDestino != null && !correoDestino.isBlank()) {
                gmailEmailService.sendPDF(correoDestino, "Tu entrada en PDF", entrada.getId());
            }
            tokenDAO.delete(token);
            pagoDAO.save(pago);
        }
        return "ok";
    }

}
