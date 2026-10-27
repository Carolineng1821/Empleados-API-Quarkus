package com.example;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class EmpleadoMapper {
    public EmpleadoResponse toResponse(Empleado empleado) {
        return new EmpleadoResponse(
                empleado.id,
                empleado.nombre,
                empleado.departamento,
                empleado.puesto,
                empleado.salario
        );
    }

    public Empleado toEntity(EmpleadoRequest datos) {
        Empleado empleado = new Empleado();
        actualizar(empleado,datos);
        return empleado;
    }

    public void actualizar(Empleado empleado, EmpleadoRequest datos) {
        empleado.nombre = datos.nombre;
        empleado.departamento = datos.departamento;
        empleado.puesto = datos.puesto;
        empleado.salario = datos.salario;
    }

    public void aplicarCambios(
            Empleado empleado,
            String departamento,
            String puesto,
            BigDecimal salario) {
        if (departamento != null) {
            empleado.departamento = departamento;
        }
        if (puesto != null) {
            empleado.puesto = puesto;
        }
        if (salario != null) {
            empleado.salario = salario;
        }
    }
}
