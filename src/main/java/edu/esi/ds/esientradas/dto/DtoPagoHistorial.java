package edu.esi.ds.esientradas.dto;

import java.time.LocalDateTime;

public class DtoPagoHistorial {

    private Long id;
    private Long costo;
    private LocalDateTime fechaPago;
    private Long idEntrada;
    private String artista;
    private LocalDateTime fechaEspectaculo;
    private String escenarioNombre;

    public DtoPagoHistorial() {
    }

    public DtoPagoHistorial(Long id, Long costo, LocalDateTime fechaPago, Long idEntrada, String artista, LocalDateTime fechaEspectaculo, String escenarioNombre) {
        this.id = id;
        this.costo = costo;
        this.fechaPago = fechaPago;
        this.idEntrada = idEntrada;
        this.artista = artista;
        this.fechaEspectaculo = fechaEspectaculo;
        this.escenarioNombre = escenarioNombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCosto() {
        return costo;
    }

    public void setCosto(Long costo) {
        this.costo = costo;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Long getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(Long idEntrada) {
        this.idEntrada = idEntrada;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public LocalDateTime getFechaEspectaculo() {
        return fechaEspectaculo;
    }

    public void setFechaEspectaculo(LocalDateTime fechaEspectaculo) {
        this.fechaEspectaculo = fechaEspectaculo;
    }

    public String getEscenarioNombre() {
        return escenarioNombre;
    }

    public void setEscenarioNombre(String escenarioNombre) {
        this.escenarioNombre = escenarioNombre;
    }
}