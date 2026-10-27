
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

import java.util.List;

@Path("/empleados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmpleadosResource {

    @Inject
    JsonWebToken jwt;
    @Inject
    EmpleadoService empleadoService;

//BUSCAR
    @Operation(summary = "Buscar empleados")
    @APIResponse(responseCode = "200", description = "Lista obtenida")
    @RolesAllowed("ADMIN")
    @GET
    public List<EmpleadoResponse> listar() {
       return empleadoService.listar();
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
        EmpleadoResponse empleado = empleadoService.buscarPorId(id);
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
    public Response crear(@Valid EmpleadoRequest datos) {
        Long empleadoId = empleadoService.crear(datos);
        URI location = URI.create("/empleados/" + empleadoId);
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
    public Response actualizar(@PathParam("id") Long id, @Valid EmpleadoRequest datos) {
        EmpleadoResponse actualizado = empleadoService.actualizar(id, datos);
        if (actualizado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorEmpleadoNoEncontrado(id))
                    .build();
        }
        return Response.ok(actualizado).build();
    }


//ACTUALIZAR PARCIAL
    @Operation(summary = "Actualizar parcialmente un empleado")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Actualizado",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = EmpleadoResponse.class)
            )),
            @APIResponse(responseCode = "400", description = "Datos inválidos"),
            @APIResponse(responseCode = "404", description = "No encontrado")
    })
    @RolesAllowed("ADMIN")
     @PATCH
     @Path("/{id}")
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

         String nuevoDepartamento = null;
         String nuevoPuesto = null;
         BigDecimal nuevoSalario = null;

         if (datos.containsKey("departamento")) {
             if (datos.isNull("departamento")
             || datos.get("departamento").getValueType()
             != JsonValue.ValueType.STRING) {
                 return Response.status(Response.Status.BAD_REQUEST)
                         .build();
             }
             nuevoDepartamento = datos.getString("departamento");
             if (nuevoDepartamento.isBlank()) {
                 return Response.status(Response.Status.BAD_REQUEST)
                         .build();
             }
         }

        if (datos.containsKey("puesto")) {
            if (datos.isNull("puesto")
            || datos.get("puesto").getValueType()
            != JsonValue.ValueType.STRING) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .build();
            }
            nuevoPuesto = datos.getString("puesto");
            if (nuevoPuesto.isBlank()) {
                return  Response.status(Response.Status.BAD_REQUEST)
                         .build();
            }
        }

         if (datos.containsKey("salario")) {
             if (datos.isNull("salario")
             || datos.get("salario").getValueType()
             != JsonValue.ValueType.NUMBER) {
                 return Response.status(Response.Status.BAD_REQUEST)
                         .build();
             }

             JsonNumber numeroSalario = datos.getJsonNumber("salario");
             nuevoSalario = numeroSalario.bigDecimalValue();

             if (nuevoSalario.compareTo(BigDecimal.ZERO) <= 0
                     || nuevoSalario.compareTo(new BigDecimal("80000")) >= 0) {
                 return Response.status(Response.Status.BAD_REQUEST)
                         .build();
             }
         }
        EmpleadoResponse actualizado = empleadoService.actualizarParcial(
                id, nuevoDepartamento,nuevoPuesto, nuevoSalario);
         if (actualizado == null) {
             return Response.status(Response.Status.NOT_FOUND)
                     .entity(errorEmpleadoNoEncontrado(id))
                     .build();
         }

         return Response.ok(actualizado).build();
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
    public Response eliminar(@PathParam("id") Long id) {
        if (!empleadoService.eliminar(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorEmpleadoNoEncontrado(id))
                    .build();
        }
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
