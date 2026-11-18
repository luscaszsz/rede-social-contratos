package org.caixa.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class ContratoResourceTest {

    @Test
    void listaContratosComSucesso() {
        given().when().get("/api/contracts")
                .then().statusCode(200).body("size()", greaterThanOrEqualTo(5));
    }

    @Test
    void buscaContratoExistenteComSucesso() {
        given().when().get("/api/contracts/2")
                .then().statusCode(200)
                .body("id", equalTo(2))
                .body("title", equalTo("Analista de Sistemas"));
    }

    @Test
    void buscaContratoInexistenteRetorna404() {
        given().when().get("/api/contracts/999999")
                .then().statusCode(404)
                .body("status", equalTo(404));
    }

    @Test
    void criaContratoComSucesso() {
        given().auth().preemptive().basic("adm", "adm123")
                .contentType(ContentType.JSON)
                .body("{\"titulo\":\"Pessoa Desenvolvedora de Testes\",\"empresa\":\"Laboratório de Qualidade\",\"responsavel\":\"FUTURO\",\"dataInicio\":\"2027-04-01\",\"situacao\":\"FUTURO\" }")
                .when().post("/api/contracts")
                .then().statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("title", equalTo("Pessoa Desenvolvedora de Testes"));
    }

    @Test
    void criaContratoInvalidoRetorna400() {
        given().auth().preemptive().basic("user", "user123")
                .contentType(ContentType.JSON)
                .body("{\"title\":\"\",\"company\":\"\",\"status\":null,\"startDate\":null}")
                .when().post("/api/contracts")
                .then().statusCode(400)
                .body("status", equalTo(400));
    }

}
