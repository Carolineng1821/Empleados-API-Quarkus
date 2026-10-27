package com.example;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class EmpleadoService {

    @Inject
    EmpleadoMapper mapper;
    public List<EmpleadoResponse> listar() {
        return Empleado.<Empleado>listAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public EmpleadoResponse buscarPorId(Long id) {
        Empleado empleado = Empleado.findById(id);
        return empleado == null ? null : mapper.toResponse(empleado);
    }

    @Transactional
    public long crear(EmpleadoRequest datos) {
        Empleado empleado = mapper.toEntity(datos);
        empleado.persist();
        return empleado.id;
    }
    @Transactional
    public EmpleadoResponse actualizar(Long id, EmpleadoRequest datos) {
        Empleado empleado = Empleado.findById(id);
        if (empleado == null) {
            return null;
        }
        mapper.actualizar(empleado, datos);
        return mapper.toResponse(empleado);
    }

    @Transactional
    public EmpleadoResponse actualizarParcial(
            Long id,
            String departamento,
            String puesto,
            BigDecimal salario) {
        Empleado empleado = Empleado.findById(id);
        if (empleado == null) {
            return null;
        }
        mapper.aplicarCambios(empleado, departamento, puesto, salario);
        return mapper.toResponse(empleado);
    }

    @Transactional
    public boolean eliminar(Long id) {
        Empleado empleado = Empleado.findById(id);
        if (empleado == null) {
            return false;
        }
        empleado.delete();
        return true;
    }
}
