package edu.esi.ds.esientradas.dto;

import java.time.LocalDateTime;

public class DtoEspectaculo {

    private String artista;
    private LocalDateTime fecha;
    private String escenario;

    public void setArtista(String artista) {
       this.artista = artista;
    }

    public String getArtista() {
        return this.artista;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFecha() {
        return this.fecha;
    }

    public void setEscenario(String nombre) {
        this.escenario = nombre;
    }

    public String getEscenario() {
        return this.escenario;
    }

}
