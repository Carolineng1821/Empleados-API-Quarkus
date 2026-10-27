package com.example;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class EmpleadoRequest {
    @NotBlank
    public String nombre;

    @NotBlank
    public String departamento;

    @NotBlank
    public String puesto;

    @NotNull
    @DecimalMin(value = "0", message = "El salario debe ser mayor que 0 pesos", inclusive = false)
    @DecimalMax(value = "80000", message = "El salario debe ser menor que 80,000 pesos", inclusive = false)
    public BigDecimal salario;
}
