package org.caixa.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@QuarkusTest
class ObservabilidadeContratoTest {

    @Test
    void readinessIncluiSaudeDoBanco() {
        given().when().get("/q/health/ready")
                .then().statusCode(200)
                .body("status", equalTo("UP"))
                .body("checks.name", hasItem("Database connections health check"));
    }

    @Test
    void cadastroRegistraContadorETempo() {
        given().auth().preemptive().basic("user", "user123")
                .contentType(ContentType.JSON)
                .body("{\"title\":\"Contrato de observabilidade\",\"company\":\"Métricas Brasil\",\"status\":\"FUTURO\",\"startDate\":\"2027-08-01\"}")
                .when().post("/api/contracts")
                .then().statusCode(201);

        given().when().get("/q/metrics")
                .then().statusCode(200)
                .body(containsString("contracts_created_total"))
                .body(containsString("contracts_cadastro_seconds_count"));
    }
}
