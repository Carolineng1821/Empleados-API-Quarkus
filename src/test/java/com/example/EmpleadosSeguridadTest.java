package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class EmpleadosSeguridadTest {

    @Test
    void sinToken401() {
        given()
                .when()
                .get("/empleados")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "empleado", roles = {"EMPLEADO"})
    void empleadoNoPuedeListarModificar() {
        given()
                .get("/empleados")
                .then()
                .statusCode(403);
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                      "nombre" : "prueba",
                      "departamento" : "TI",
                      "puesto" : "QA",
                      "salario": 25000
                    }
                    """)
                .post("/empleados")
                .then()
                .statusCode(403);
        given()
        .contentType(ContentType.JSON)
                .body("""
                {
                  "puesto" : "QA"
                }
                """)
                .patch("/empleados/1")
                .then()
                .statusCode(403);
        given()
        .contentType(ContentType.JSON)
                .body("""
                        {
                          "nombre": "Prueba",
                          "departamento": "TI",
                          "puesto": "QA",
                          "salario": 25000
                        }
                """)
                .put("/empleados/1")
                .then()
                .statusCode(403);
        given()
                .delete("/empleados/1")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "empleado-999999", roles = {"EMPLEADO"})
    @JwtSecurity(claims = {
            @Claim(key = "sub", value = "999999")
    })
    void empleadoSoloPuedeConsultarElIdDeSuSub() {
        given()
                .get("/empleados/999998")
                .then()
                .statusCode(403);

        given()
                .get("/empleados/999999")
                .then()
                .statusCode(404);
    }
}
