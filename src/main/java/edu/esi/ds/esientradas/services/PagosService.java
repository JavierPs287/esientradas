package edu.esi.ds.esientradas.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import edu.esi.ds.esientradas.dao.EntradaDAO;
import edu.esi.ds.esientradas.dao.TokenDAO;
import edu.esi.ds.esientradas.model.Estado;
import edu.esi.ds.esientradas.model.Token;

@Service
public class PagosService {

    // @Autowired
    // ConfiguracionDAO configuracionDAO;

    @Autowired
    private EntradaDAO entradaDAO;

    @Autowired
    private TokenDAO tokenDAO;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private static final String secretKey = "key";

    public String prepararPago(Map<String,Object> infoPago) {
        try{
            Stripe.apiKey = stripeSecretKey;
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
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error al preparar el pago: ");
        }
    }


    //TODO quitar la logica
    public String confirmarPago(String sessionId) {
        List<Token> tokens = tokenDAO.findAllBySessionId(sessionId);
        for (Token token : tokens) {
            entradaDAO.updateEstado(token.getEntrada().getId(), Estado.VENDIDA);
            tokenDAO.delete(token);
        }
        return "ok";
    }

}
