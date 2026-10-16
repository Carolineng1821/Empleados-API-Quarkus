package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class EmpleadosResourceTest {

    @Test
    void creaEmpleadoNombreValido() {
      Response respuesta = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                        "nombre": "Carolina"
                        }
                        """)
                .when()
                .post("/empleados")
                .then()
                .statusCode(201)
                .extract()
                .response();

        String location = respuesta.getHeader("Location");
        assertTrue(location.matches(".*/empleados/\\d+$"));
        String[] partes = location.split("/");
        Long id = Long.valueOf(partes[partes.length - 1]);

        given()
                .when()
                .get("/empleados/" + id)
                .then()
                .statusCode(200)
                .body("nombre", equalTo("Carolina"));
    }

    @Test
    void rechazaEmpleadoNombreVacio() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"nombre": ""}
                """)
                .when()
                .post("/empleados")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Error de validación"))
                .body("mensaje", equalTo("Hay campos inválidos"))
                .body("detalles.size()", equalTo(1))
                .body("detalles[0].campo", equalTo("nombre"))
                .body("detalles[0].mensaje", equalTo("no debe estar vacío"));

    }

    @Test
    void listarEmpleados(){

        given()
                .contentType(ContentType.JSON)
                .body("""
                      {"nombre": "Empleado GET"}
                      """)
                .when()
                .post("/empleados")
                .then()
                .statusCode(201);

        given()
                .when()
                .get("/empleados")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", not(empty()))
                .body("find {it.nombre == 'Empleado GET' }.id", notNullValue());
    }

    @Test
    void actualizarEmpleado() {
        given()
        .contentType(ContentType.JSON)
                .body("""
                    {
                        "nombre": "Ana"
                    }
                """)
                .when()
                .post("/empleados")
                .then()
                .statusCode(201);
        Long id = ((Number) given()
                .when()
                .get("/empleados")
                .then()
                .statusCode(200)
                .extract()
                .path("find {it.nombre == 'Ana'}.id"))
                        .longValue();
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"nombre": "Ana López"}
                """)
                .when()
                .put("/empleados/" + id)
                .then()
                .statusCode(200)
                .body("nombre",equalTo("Ana López"));
    }

    @Test
    void eliminarEmpleado() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "nombre": "Empleado para eliminar"
                    }
                """)
                .when()
                .post("/empleados")
                .then()
                .statusCode(201);
        Long id = ((Number) given()
                .when()
                .get("/empleados")
                .then()
                .statusCode(200)
                .extract()
                .path("find {it.nombre == 'Empleado para eliminar'}.id"))
                .longValue();
        given()
                .when()
                .delete("/empleados/" + id)
                .then()
                .statusCode(204);
        given()
                .when()
                .get("/empleados/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void eliminarEmpleadoInexistente() {
        given()
                .when()
                .delete("/empleados/123")
                .then()
                .statusCode(404);
    }
}
