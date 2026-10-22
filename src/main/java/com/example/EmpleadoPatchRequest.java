package com.example;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

public class EmpleadoPatchRequest {
    @Schema(example = "Operaciones")
    public String departamento;
    @Schema(example = "Analista")
    public String puesto;
    @Schema (example = "32000")
    public BigDecimal salario;
}
