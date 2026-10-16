
package com.example;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.net.URI;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import java.util.ArrayList;
import java.util.List;

@Path("/empleados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmpleadosResource {


//BUSCAR
    @Operation(summary = "Buscar empleados")
    @APIResponse(responseCode = "200", description = "Lista obtenida")
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

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        Empleado empleado = Empleado.findById(id);
        if (empleado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No existe un empleado con ID: " + id)
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

    @PUT
    @Path("/{id}")
    @Transactional
    public Response actualizar(@PathParam("id") Long id, @Valid Empleado datos) {
        Empleado empleado = Empleado.findById(id);
        if (empleado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No existe un empleado con ID: " + id)
                    .build();
        }
        empleado.nombre = datos.nombre;
        return Response.ok(empleado).build();
    }


//ELIMINAR
    @Operation(summary = "Eliminar empleado")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Eliminado"),
            @APIResponse(responseCode = "404", description = "No encontrado")
    })

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response eliminar(@PathParam("id") Long id) {
        Empleado empleado = Empleado.findById(id);
        if (empleado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No existe un empleado con ID: " + id)
                    .build();
        }
        empleado.delete();
        return Response.noContent().build();
    }
}
