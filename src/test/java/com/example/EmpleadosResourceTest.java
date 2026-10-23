package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import javax.xml.stream.Location;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestSecurity(user = "admin-test", roles = {"ADMIN"})
class EmpleadosResourceTest {

    @Test
    void creaEmpleadoNombreValido() {
      Response respuesta = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                         "nombre": "Carolina",
                          "departamento": "Tecnología",
                          "puesto": "Desarrolladora",
                          "salario": 18000
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
                .body("nombre", equalTo("Carolina"))
                .body("departamento", equalTo("Tecnología"))
                .body("puesto", equalTo("Desarrolladora"));
        Response consulta = given()
                .when()
                .get("/empleados/" + id)
                .then()
                .statusCode(200)
                .body("nombre", equalTo("Carolina"))
                .body("departamento", equalTo("Tecnología"))
                .body("puesto", equalTo("Desarrolladora"))
                .extract()
                .response();

        BigDecimal salario = new BigDecimal(
                consulta.jsonPath().getString("salario")
        );

        assertEquals(
                0,
                salario.compareTo(new BigDecimal("18000.00"))
        );;
    }

    @Test
    void rechazaEmpleadoNombreVacio() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        { "nombre": "",
                           "departamento": "RH",
                           "puesto": "RH",
                           "salario": 15000}
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

                        {
                        "nombre": "Empleado GET",
                        "departamento": "Tecnología",
                        "puesto": "Desarrolladora",
                        "salario": 18000
                      }
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
    void consultaEmpleadoInexistente(){

        given()

                .when()
                .get("/empleados/99999")
                .then()
                .statusCode(404)

                .body("status", equalTo(404))
                .body("error", equalTo("Empleado no encontrado"))
                .body("mensaje", equalTo("No existe un empleado con ID: 99999"));
    }

    @Test
    void actualizarEmpleado() {
        given()
        .contentType(ContentType.JSON)
                .body("""
                    {
                        "nombre": "Ana",
                        "departamento": "RH",
                        "puesto": "RH",
                        "salario": 15000
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
                        {
                          "nombre": "Carolina",
                          "departamento": "Recursos Humanos",
                          "puesto": "Gerente",
                          "salario": 20000
                        }
                """)
                .when()
                .put("/empleados/" + id)
                .then()
                .statusCode(200)
                .body("nombre",equalTo("Carolina"))
                .body("departamento",equalTo("Recursos Humanos"))
                .body("puesto",equalTo("Gerente"))
                .body("salario",equalTo(20000));
    }

    @Test
    void actualizarEmpleadoInexistente(){
        given()
                .contentType(ContentType.JSON)
                .body("""

                        {
                        "nombre": "Carolina",
                        "departamento": "Tecnología",
                        "puesto": "Desarrolladora",
                        "salario": 18000
                      }
                      """)
                .when()
                .put("/empleados/99999")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Empleado no encontrado"))
                .body("mensaje", equalTo("No existe un empleado con ID: 99999"));
    }

    @Test
    void patchActualizacionSoloCamposEnviados(){
        String location = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                    "nombre": "Empleado PATCH valido",
                    "departamento": "TI",
                    "puesto": "Desarrollador",
                    "salario": 25000
                    }
                    """)
                .when()
                .post("/empleados")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");
        String id = location.substring(location.lastIndexOf('/') + 1);

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "puesto" : "Analista"
                    }
                    """)
                .when()
                .patch("/empleados/" + id)
                .then()
                .statusCode(200)
                .body("puesto",equalTo("Analista"));

        given()
                .when()
                .get("/empleados/" + id)
                .then()
                .statusCode(200)
                .body("nombre",equalTo("Empleado PATCH valido"))
                .body("departamento",equalTo("TI"))
                .body("puesto",equalTo("Analista"));
                Response consulta = given()
                        .when()
                        .get("/empleados/" + id)
                        .then()
                        .statusCode(200)
                        .body("puesto", equalTo("Analista"))
                        .extract()
                        .response();
                BigDecimal salario = new BigDecimal(
                        consulta.jsonPath().getString("salario"));
                assertEquals(0, salario.compareTo(new BigDecimal("25000")));
    }

    @Test
    void patchInvalidoNoGuardaCambiosParciales() {
        String location = given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                    "nombre": "Empleado PATCH invalido",
                    "departamento": "TI",
                    "puesto": "Desarrollador",
                    "salario": 25000
                    }
                    """)
                .when()
                .post("/empleados")
                .then()
                .statusCode(201)
                .extract()
                .header("Location");
        String id = location.substring(location.lastIndexOf('/') + 1);

        given()
        .contentType(ContentType.JSON)
                .body("""
                    {
                      "puesto" : "Temporal",
                      "salario" : "32000"
                    }
                    """)
                .when()
                .patch("/empleados/" + id)
                .then()
                .statusCode(400);

        given()
                .when()
                .get("/empleados/" + id)
                .then()
                .statusCode(200)
                .body("puesto",equalTo("Desarrollador"));
        Response consulta = given()
                .when()
                .get("/empleados/" + id)
                .then()
                .statusCode(200)
                .body("puesto", equalTo("Desarrollador"))
                .extract()
                .response();
        BigDecimal salario = new BigDecimal(
                consulta.jsonPath().getString("salario"));
        assertEquals(0, salario.compareTo(new BigDecimal("25000")));
    }

    @Test
    void patchEmpleadoInexistente() {
        given()
        .contentType(ContentType.JSON)
                .body("""
                    {
                      "puesto" : "Analista"
                    }
                    """)
                .when()
                .patch("/empleados/99999")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error",equalTo("Empleado no encontrado"))
                .body("mensaje", equalTo("No existe un empleado con ID: 99999"));
        ;
    }

    @Test
    void eliminarEmpleado() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "nombre": "Empleado para eliminar",
                      "departamento": "Tecnología",
                      "puesto": "Desarrolladora",
                      "salario": 18000
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
                .delete("/empleados/99999")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Empleado no encontrado"))
                .body("mensaje", equalTo("No existe un empleado con ID: 99999"));
    }

}
