
package com.example;

import jakarta.json.JsonValue;
import jakarta.json.JsonObject;
import jakarta.json.JsonNumber;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Consumes;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.net.URI;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.ArrayList;
import java.util.List;

@Path("/empleados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmpleadosResource {

    @Inject
    JsonWebToken jwt;

//BUSCAR
    @Operation(summary = "Buscar empleados")
    @APIResponse(responseCode = "200", description = "Lista obtenida")
    @RolesAllowed("ADMIN")
    @GET
    public List<Empleado> listar() {
        return Empleado.listAll();
    }



//BUSCAR POR ID
    @Operation(summary = "Buscar empleado por ID")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Encontrado"),
            @APIResponse(responseCode = "404", description = "No encontrado")
    })
    @RolesAllowed({"ADMIN", "EMPLEADO"})
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        boolean esAdmin = jwt.getGroups() != null
                && jwt.getGroups().contains("ADMIN");
        if (!esAdmin && !id.toString().equals(jwt.getSubject())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Empleado empleado = Empleado.findById(id);
        if (empleado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorEmpleadoNoEncontrado(id))
                    .build();
        }
        return Response.ok(empleado).build();
    }


//CREAR
    @Operation(summary = "Crear empleado")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Empleado creado"),
            @APIResponse(responseCode = "400", description = "Datos invalidos")
    })
    @RolesAllowed("ADMIN")
    @POST
    @Transactional
    public Response crear(@Valid Empleado empleado) {
        empleado.id = null;
        empleado.persist();

        URI location = URI.create("/empleados/" + empleado.id);
        return Response.created(location).build();
    }


//ACTUALIZAR
    @Operation(summary = "Actualizar empleado")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Actualizado"),
            @APIResponse(responseCode = "400", description = "Datos inválidos"),
            @APIResponse(responseCode = "404", description = "No encontrado")
    })
    @RolesAllowed("ADMIN")
    @PUT
    @Path("/{id}")
    @Transactional
    public Response actualizar(@PathParam("id") Long id, @Valid Empleado datos) {
        Empleado empleado = Empleado.findById(id);
        if (empleado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorEmpleadoNoEncontrado(id))
                    .build();
        }
        empleado.nombre = datos.nombre;
        empleado.departamento = datos.departamento;
        empleado.puesto = datos.puesto;
        empleado.salario = datos.salario;
        return Response.ok(empleado).build();
    }


//ACTUALIZAR PARCIAL
    @Operation(summary = "Actualizar parcialmente un empleado")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Actualizado",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = Empleado.class)
            )),
            @APIResponse(responseCode = "400", description = "Datos inválidos"),
            @APIResponse(responseCode = "404", description = "No encontrado")
    })
    @RolesAllowed("ADMIN")
     @PATCH
     @Path("/{id}")
     @Transactional
     @Consumes(MediaType.APPLICATION_JSON)
     public Response actualizarParcial(
             @PathParam("id") Long id,
             @RequestBody(
                     description = "Campos opcionales a actualizar: departamento,puesto y salario.",
                     required = true,
                     content = @Content(
                             mediaType = MediaType.APPLICATION_JSON,
                             schema = @Schema(implementation = EmpleadoPatchRequest.class
                             )
                     )
             )
             JsonObject datos) {

        if (datos.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }

        Empleado empleado = Empleado.findById(id);
         if (empleado == null) {
             return Response.status(Response.Status.NOT_FOUND)
                     .entity(errorEmpleadoNoEncontrado(id))
                     .build();
         }
         String nuevoDepartamento = null;
         String nuevoPuesto = null;
         BigDecimal nuevoSalario = null;

         if (datos.containsKey("departamento")) {
             if (datos.isNull("departamento")) {
                 return Response.status(Response.Status.BAD_REQUEST)
                         .build();
             }

             if (datos.get("departamento").getValueType()
                     != JsonValue.ValueType.STRING) {
                 return Response.status(Response.Status.BAD_REQUEST)
                         .build();
             }

             String departamento = datos.getString("departamento");
             if (departamento.isBlank()) {
                 return Response.status(Response.Status.BAD_REQUEST)
                         .build();
             }
             nuevoDepartamento = departamento;
         }

        if (datos.containsKey("puesto")) {
            if (datos.isNull("puesto")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .build();
            }

        if (datos.get("puesto").getValueType()
            != JsonValue.ValueType.STRING) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .build();
            }

        String puesto = datos.getString("puesto");
        if (puesto.isBlank()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .build();
            }
            nuevoPuesto = puesto;
        }

         if (datos.containsKey("salario")) {
             if (datos.isNull("salario")) {
                 return Response.status(Response.Status.BAD_REQUEST)
                         .build();
             }
             if (datos.get("salario").getValueType() != JsonValue.ValueType.NUMBER) {
                 return Response.status(Response.Status.BAD_REQUEST)
                         .build();
             }
             JsonNumber numeroSalario = datos.getJsonNumber("salario");
            BigDecimal salario = numeroSalario.bigDecimalValue();

             if (salario.compareTo(BigDecimal.ZERO) <= 0
                     || salario.compareTo(new BigDecimal("80000")) >= 0) {
                 return Response.status(Response.Status.BAD_REQUEST)
                         .build();
             }
             nuevoSalario = salario;
         }

         if (nuevoDepartamento != null ){
             empleado.departamento = nuevoDepartamento;
         }
         if (nuevoPuesto != null ){
             empleado.puesto = nuevoPuesto;
         }
         if (nuevoSalario != null ) {
             empleado.salario = nuevoSalario;
         }

         return Response.ok(empleado).build();
     }


//ELIMINAR
    @Operation(summary = "Eliminar empleado")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Eliminado"),
            @APIResponse(responseCode = "404", description = "No encontrado")
    })
    @RolesAllowed("ADMIN")
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Long id) {
        Empleado empleado = Empleado.findById(id);
        if (empleado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorEmpleadoNoEncontrado(id))
                    .build();
        }
        empleado.delete();
        return Response.noContent().build();
    }

//ERROR CENTRALIZADO
    private RespuestaErrorRecurso errorEmpleadoNoEncontrado(Long id) {
        return new RespuestaErrorRecurso(
                404,
                "Empleado no encontrado",
                "No existe un empleado con ID: " + id
        );
    }
}
