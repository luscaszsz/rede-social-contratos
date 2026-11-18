package org.caixa.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
class ContratoResourceTest {
    @Test
    void listaContratosIniciais() {
        given().when().get("/api/contracts").then().statusCode(200).body("", hasSize(5));
    }
}
