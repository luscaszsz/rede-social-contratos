package org.caixa.mapper;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.time.Instant;

import org.caixa.dto.ApiErro;
import org.caixa.exception.ConteudoDaPublicacaoInalteradoException;
import org.caixa.exception.LimiteDePublicacoesPorContratoException;
import org.caixa.exception.ContratoNaoEncontradoException;
import org.caixa.exception.ContratosIndisponiveisException;

public class MapeadorDeErros {
    @ServerExceptionMapper
    Response validation(ConstraintViolationException exception, @Context UriInfo uriInfo) {
        return error(400, "Bad Request", exception.getMessage(), uriInfo);
    }

    @ServerExceptionMapper
    Response missing(NotFoundException exception, @Context UriInfo uriInfo) {
        return error(404, "Not Found", exception.getMessage() == null ? "Recurso não encontrado." : exception.getMessage(), uriInfo);
    }

    @ServerExceptionMapper
    Response contratoNaoEncontrado(ContratoNaoEncontradoException exception, @Context UriInfo uriInfo) {
        return error(404, "Not Found", exception.getMessage(), uriInfo);
    }

    @ServerExceptionMapper
    Response contratosIndisponiveis(ContratosIndisponiveisException exception, @Context UriInfo uriInfo) {
        return error(503, "Service Unavailable", exception.getMessage(), uriInfo);
    }

    @ServerExceptionMapper
    Response conteudoInalterado(ConteudoDaPublicacaoInalteradoException exception, @Context UriInfo uriInfo) {
        return error(409, "Conflict", exception.getMessage(), uriInfo);
    }

    @ServerExceptionMapper
    Response limiteDePublicacoes(LimiteDePublicacoesPorContratoException exception, @Context UriInfo uriInfo) {
        return error(422, "Unprocessable Entity", exception.getMessage(), uriInfo);
    }

    private Response error(int status, String error, String message, UriInfo uriInfo) {
        return Response.status(status).entity(new ApiErro(Instant.now(), status, error, message, uriInfo.getPath())).build();
    }
}
