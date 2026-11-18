package org.caixa.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.caixa.client.ClienteHealthContratos;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;

@QuarkusTest
class ObservabilidadePublicacaoTest {
    @InjectMock @RestClient ClienteHealthContratos clienteHealthContratos;

    @BeforeEach
    void prepararServicoDeContratos() {
        when(clienteHealthContratos.verificarProntidao()).thenReturn("{\"status\":\"UP\"}");
    }

    @Test
    void livenessExplicaOLimiteConfigurado() {
        given().when().get("/q/health/live")
                .then().statusCode(200)
                .body("status", equalTo("UP"))
                .body("checks.name", hasItem("feed-publicacoes"))
                .body("checks.data.'limite-por-contrato'", hasItem(3));
    }

    @Test
    void readinessIncluiBancoEServicoDeContratos() {
        given().when().get("/q/health/ready")
                .then().statusCode(200)
                .body("status", equalTo("UP"))
                .body("checks.name", hasItem("Database connections health check"))
                .body("checks.name", hasItem("contracts-service"));
    }

    @Test
    void cadastroRegistraContadorETempo() {
        given().auth().preemptive().basic("user", "user123")
                .contentType(ContentType.JSON)
                .body("{\"content\":\"Publicação de observabilidade.\"}")
                .when().post("/api/posts")
                .then().statusCode(201);

        given().when().get("/q/metrics")
                .then().statusCode(200)
                .body(containsString("posts_created_total"))
                .body(containsString("feed_publicacoes_cadastro_seconds_count"));
    }
}
