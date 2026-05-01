package edu.esi.ds.esientradas.dto;

import java.time.LocalDateTime;

public class DtoEntrada {
    private Long id;
    private Long precio;
    private String artista;
    private LocalDateTime fecha;
    private String escenarioNombre;

    public DtoEntrada() {}

    public DtoEntrada(Long id, Long precio,String artista, LocalDateTime fecha, String escenarioNombre) {
        this.id = id;
        this.precio = precio;
        this.artista = artista;
        this.fecha = fecha;
        this.escenarioNombre = escenarioNombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrecio() {
        return precio;
    }

    public void setPrecio(Long precio) {
        this.precio = precio;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEscenarioNombre() {
        return escenarioNombre;
    }

    public void setEscenarioNombre(String escenarioNombre) {
        this.escenarioNombre = escenarioNombre;
    }
}
