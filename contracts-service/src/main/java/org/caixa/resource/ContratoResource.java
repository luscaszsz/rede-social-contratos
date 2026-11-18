package org.caixa.resource;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import org.caixa.dto.ContratoRequest;
import org.caixa.dto.ContratoResponse;
import org.caixa.model.Contrato;
import org.caixa.service.ContratoService;

@Path("/api/contracts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContratoResource {
    @jakarta.inject.Inject
    ContratoService servico;

    @GET
    public List<ContratoResponse> listar() {
        return servico.listar();
    }

    @GET
    @Path("/{id}")
    public ContratoResponse buscar(@PathParam("id") Long id) {
        return servico.buscar(id);
    }

    @POST
    @RolesAllowed("user")
    @Transactional
    @Counted(value = "contracts.created")
    @Timed(value = "contracts.cadastro", description = "Tempo de resposta do cadastro de contrato")
    public Response criar(@Valid ContratoRequest requisicao, @Context UriInfo informacoesUri) {
        Contrato contrato = servico.criar(requisicao);
        URI localizacao = informacoesUri.getAbsolutePathBuilder().path(contrato.id.toString()).build();
        return Response.created(localizacao).entity(ContratoResponse.de(contrato)).build();
    }
}
