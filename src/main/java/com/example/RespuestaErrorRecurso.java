package com.example;

public class RespuestaErrorRecurso {
    public int status;
    public String error;
    public String mensaje;

    public RespuestaErrorRecurso(int status, String error, String mensaje) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
    }
}