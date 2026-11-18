package org.caixa.resource;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.caixa.client.ClienteContratos;
import org.caixa.dto.ResumoContrato;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@QuarkusTest
class PublicacaoResourceTest {
    private static final String USUARIO = "user";
    private static final String SENHA = "user123";

    @InjectMock
    @RestClient
    ClienteContratos clienteContratos;

    @BeforeEach
    void prepararClienteDeContratos() {
        reset(clienteContratos);
        when(clienteContratos.listar()).thenReturn(List.of(resumoContrato()));
        when(clienteContratos.buscar(2L)).thenReturn(resumoContrato());
    }

    @Test
    void listaPublicacoesComSucesso() {
        given().when().get("/api/posts")
                .then().statusCode(200).body("size()", greaterThanOrEqualTo(4));
    }

    @Test
    void buscaPublicacaoExistenteComSucesso() {
        given().when().get("/api/posts/1")
                .then().statusCode(200)
                .body("id", equalTo(1))
                .body("content", notNullValue());
    }

    @Test
    void buscaPublicacaoInexistenteRetorna404() {
        given().when().get("/api/posts/999999")
                .then().statusCode(404)
                .body("status", equalTo(404));
    }

    @Test
    void listaContratosDisponiveisComSucesso() {
        given().when().get("/api/posts/available-contracts")
                .then().statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].id", equalTo(2));
    }

    @Test
    void listaContratosDisponiveisUsaFallbackQuandoServicoFalha() {
        doThrow(new RuntimeException("Indisponível")).when(clienteContratos).listar();

        given().when().get("/api/posts/available-contracts")
                .then().statusCode(200)
                .body("", empty());
    }

    @Test
    void criaPublicacaoComSucesso() {
        given().auth().preemptive().basic(USUARIO, SENHA)
                .contentType(ContentType.JSON)
                .body("{\"content\":\"Publicação criada durante o teste.\",\"contractId\":2}")
                .when().post("/api/posts")
                .then().statusCode(201)
                .header("Location", notNullValue())
                .body("content", equalTo("Publicação criada durante o teste."))
                .body("contract.id", equalTo(2));
    }

    @Test
    void criaPublicacaoInvalidaRetorna400() {
        given().auth().preemptive().basic(USUARIO, SENHA)
                .contentType(ContentType.JSON)
                .body("{\"content\":\"\",\"contractId\":2}")
                .when().post("/api/posts")
                .then().statusCode(400)
                .body("status", equalTo(400));
    }

    @Test
    void criaPublicacaoSemCredencialRetorna401() {
        given().contentType(ContentType.JSON)
                .body("{\"content\":\"Publicação sem credencial.\"}")
                .when().post("/api/posts")
                .then().statusCode(401);
    }

    @Test
    void atualizaPublicacaoComSucesso() {
        Long id = criarPublicacaoSemContrato();

        given().auth().preemptive().basic(USUARIO, SENHA)
                .contentType(ContentType.JSON)
                .body("{\"content\":\"Conteúdo atualizado.\",\"contractId\":2}")
                .when().put("/api/posts/" + id)
                .then().statusCode(200)
                .body("id", equalTo(id.intValue()))
                .body("content", equalTo("Conteúdo atualizado."))
                .body("contract.id", equalTo(2));
    }

    @Test
    void atualizaPublicacaoInexistenteRetorna404() {
        given().auth().preemptive().basic(USUARIO, SENHA)
                .contentType(ContentType.JSON)
                .body("{\"content\":\"Conteúdo atualizado.\"}")
                .when().put("/api/posts/999999")
                .then().statusCode(404)
                .body("status", equalTo(404));
    }

    @Test
    void removePublicacaoComSucesso() {
        Long id = criarPublicacaoSemContrato();

        given().auth().preemptive().basic(USUARIO, SENHA)
                .when().delete("/api/posts/" + id)
                .then().statusCode(204);
    }

    @Test
    void removePublicacaoInexistenteRetorna404() {
        given().auth().preemptive().basic(USUARIO, SENHA)
                .when().delete("/api/posts/999999")
                .then().statusCode(404)
                .body("status", equalTo(404));
    }

    private Long criarPublicacaoSemContrato() {
        return given().auth().preemptive().basic(USUARIO, SENHA)
                .contentType(ContentType.JSON)
                .body("{\"content\":\"Publicação temporária.\"}")
                .when().post("/api/posts")
                .then().statusCode(201)
                .extract().path("id");
    }

    private static ResumoContrato resumoContrato() {
        return new ResumoContrato(2L, "Analista de Sistemas", "Empresa Pública", "EM_ANDAMENTO", LocalDate.of(2025, 2, 1), null);
    }
}
