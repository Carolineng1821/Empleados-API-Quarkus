package com.example;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class Empleado extends PanacheEntity {
    @NotBlank
    public String nombre;
    @NotBlank
    public String departamento;
    @NotBlank
    public String puesto;
    @NotNull
    @DecimalMin(value = "0", message = "El salario debe ser mayor que 0", inclusive = false)
    @DecimalMax(value = "80000", message = "El salario debe ser menor que 80,000 peso", inclusive = false)
    public BigDecimal salario;

}
