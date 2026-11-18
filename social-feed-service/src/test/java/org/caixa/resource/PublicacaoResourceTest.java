package org.caixa.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
class PublicacaoResourceTest {
    @Test
    void listaPublicacoesIniciais() {
        given().when().get("/api/posts").then().statusCode(200).body("", hasSize(4));
    }

    @Test
    void pedeCredenciais() {
        given().contentType("application/json").body("{\"content\":\"Teste\"}").when().post("/api/posts").then().statusCode(401);
    }
}
