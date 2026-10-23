package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class EmpleadosJwtIntegracionTest {
    @Test
    public void tokenFirmadoAdminPuedeListar() {
        String token = Jwt.issuer("empleados-api")
                .subject("admin-test")
                .groups("ADMIN")
                .sign();
        given()
                .auth().oauth2(token)
                .get("/empleados")
                .then()
                .statusCode(200);
    }

    @Test
    void tokenFirmadoEmpleadoSoloConsultaSuId() {
        String token = Jwt.issuer("empleados-api")
                .subject("99999")
                .groups("EMPLEADO")
                .sign();
        given()
        .auth().oauth2(token)
                .get("/empleados/99999")
                .then()
                .statusCode(404);
        given()
        .auth().oauth2(token)
                .get("/empleados/99998")
                .then()
                .statusCode(403);
    }

    @Test
    void tokenMalFormadoRegresa401() {
        given()
        .auth().oauth2("token-invalido")
                .get("/empleados")
                .then()
                .statusCode(401);
    }
}
