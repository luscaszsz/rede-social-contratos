package org.caixa.client;

import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

import org.caixa.dto.ResumoContrato;
import org.caixa.exception.ContratoNaoEncontradoException;

@Path("/api/contracts")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "contracts")
public interface ClienteContratos {
    @GET
    List<ResumoContrato> listar();

    @GET
    @Path("/{id}")
    ResumoContrato buscar(@PathParam("id") Long id);

    @ClientExceptionMapper
    static RuntimeException mapear(Response resposta) {
        return resposta.getStatus() == 404 ? new ContratoNaoEncontradoException() : null;
    }
}
