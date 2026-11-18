package org.caixa.resource;

import io.micrometer.core.annotation.Timed;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.caixa.dto.ChavePixResponse;
import org.caixa.dto.PublicacaoRequest;
import org.caixa.dto.TrocaConteudoRequest;
import org.caixa.enums.TipoChave;
import org.caixa.model.PublicacaoModel;
import org.caixa.service.ChavePixService;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;
import java.util.List;

import static org.caixa.config.Papeis.*;

/**
 * Recurso REST das chaves. Só traduz HTTP para chamadas ao service (nível 2 de Richardson:
 * verbo certo, status certo, recurso identificado pela URI).
 */
@Path("/chaves")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@DenyAll
public class FeedResource {

    @Inject
    ChavePixService service;

    @POST
    @Timed(value = "pix.chaves.cadastro", description = "Tempo de resposta do cadastro de chave")
    @RolesAllowed(TITULAR)
    public RestResponse<ChavePixResponse> cadastrar(PublicacaoRequest request, @Context UriInfo uriInfo) {
        PublicacaoModel chave = service.cadastrar(request);
        URI location = uriInfo.getAbsolutePathBuilder().path(chave.id.toString()).build();
        return RestResponse.ResponseBuilder.<ChavePixResponse>created(location).entity(ChavePixResponse.de(chave)).build();
    }

    @GET
    @RolesAllowed({TITULAR, ATENDENTE, AUDITOR})
    public List<ChavePixResponse> listar(@QueryParam("titular") String titularCpf) {
        return service.listar(titularCpf).stream().map(ChavePixResponse::de).toList();
    }

    @GET
    @Path("/tipos")
    @PermitAll
    public List<TipoChave> tipos() {
        return List.of(TipoChave.values());
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({TITULAR, ATENDENTE})
    public void remover(@PathParam("id") Long id) {
        service.remover(id);
    }

    @GET
    @Path("/{id}")
    public ChavePixResponse buscar(@PathParam("id") Long id) {
        return ChavePixResponse.de(service.buscar(id));

    }


    @PUT
    @Path("/{id}")
    public ChavePixResponse trocarConteudoPublicacao(@PathParam("id") Long id, TrocaConteudoRequest request) {
        return ChavePixResponse.de(service.trocarConteudoPublicacao(id, request));

    }
}
