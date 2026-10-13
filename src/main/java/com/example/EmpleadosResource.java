
package com.example;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.net.URI;

import java.util.ArrayList;
import java.util.List;

@Path("/empleados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmpleadosResource {

    @GET
    public List<Empleado> listar() {
        return Empleado.listAll();
    }

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



    @POST
    @Transactional
    public Response crear(@Valid Empleado empleado) {
        empleado.id = null;
        empleado.persist();

        URI location = URI.create("/empleados/" + empleado.id);
        return Response.created(location).build();
    }

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
