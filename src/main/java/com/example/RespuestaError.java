package com.example;

import java.util.ArrayList;
import java.util.List;

public class RespuestaError {
    public int status;
    public String error;
    public String mensaje;
    public List<DetalleValidacion> detalles;

    public RespuestaError(int status, String error, String mensaje, List<DetalleValidacion> detalles) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        this.detalles = detalles;

    }
}
