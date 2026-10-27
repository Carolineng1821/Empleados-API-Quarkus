package com.example;

import java.math.BigDecimal;

public record EmpleadoResponse (
    Long id,
    String nombre,
    String departamento,
    String puesto,
    BigDecimal salario
) {

}
