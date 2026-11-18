package org.caixa.resource;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

import org.caixa.dto.ResumoContrato;
import org.caixa.dto.PublicacaoRequest;
import org.caixa.dto.PublicacaoResponse;
import org.caixa.model.Publicacao;
import org.caixa.service.PublicacaoService;
import org.caixa.usuarios.Papeis;

@Path("/api/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicacaoResource {
    @Inject
    PublicacaoService service;

    @GET
    public List<PublicacaoResponse> listar() {
        return service.listar().stream().map(PublicacaoResponse::de).toList();
    }

    @GET
    @Path("/available-contracts")
    public List<ResumoContrato> contratosDisponiveis() {
        return service.contratosDisponiveis();
    }

    @GET
    @Path("/{id}")
    public PublicacaoResponse buscar(@PathParam("id") Long id) {
        return PublicacaoResponse.de(service.buscar(id));
    }

    @POST
    @RolesAllowed("user")
    @Counted(value = "posts.created")
    @Timed(value = "feed.publicacoes.cadastro", description = "Tempo de resposta do cadastro de publicação")
    public Response criar(@Valid PublicacaoRequest request, @Context UriInfo uriInfo) {
        Publicacao publicacao = service.criar(request);
        URI location = uriInfo.getAbsolutePathBuilder().path(publicacao.id.toString()).build();
        return Response.created(location).entity(PublicacaoResponse.de(publicacao)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("user")
    public PublicacaoResponse atualizar(@PathParam("id") Long id, @Valid PublicacaoRequest request) {
        return PublicacaoResponse.de(service.atualizar(id, request));
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({Papeis.USUARIO, Papeis.ADMINISTRADOR})
    public Response remover(@PathParam("id") Long id) {
        service.remover(id);
        return Response.noContent().build();
    }
}
