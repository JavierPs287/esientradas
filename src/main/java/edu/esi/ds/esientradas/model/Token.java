package edu.esi.ds.esientradas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Token {

    @Id @Column(length = 36)
    private String valor;

    private Instant expiracion; //Hora de expiración del token

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_id", referencedColumnName = "id")
    private Entrada entrada;

    private String sessionId;

    public Token() {
        this.valor = UUID.randomUUID().toString();
        this.expiracion = Instant.now().plusSeconds(600); // El token expira en 10 minutos
    }

    public String getValor() {
        return valor;
    }

    public Instant getExpiracion() {
        return expiracion;
    }

    public Entrada getEntrada() {
        return entrada;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}
