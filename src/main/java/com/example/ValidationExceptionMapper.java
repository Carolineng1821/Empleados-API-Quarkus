package com.example;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;

@Provider
public class ValidationExceptionMapper
        implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
       List<DetalleValidacion> detalles = new ArrayList<>();
       for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
           detalles.add(new DetalleValidacion(
                   violation.getPropertyPath().toString().substring(violation.getPropertyPath().toString().lastIndexOf(".")+1),
                   violation.getMessage()));
       }
       RespuestaError respuesta = new RespuestaError(
               400,
               "Hay campos inválidos",
               detalles
       );
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(respuesta)
                .build();
    }
}
