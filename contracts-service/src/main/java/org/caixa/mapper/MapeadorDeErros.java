package org.caixa.mapper;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import java.time.Instant;
import org.caixa.dto.ApiErro;

public class MapeadorDeErros {

    @ServerExceptionMapper Response validacao(ConstraintViolationException exception, @Context UriInfo uriInfo) {
        return error(400, "Bad Request", exception.getMessage(), uriInfo);
    }

    @ServerExceptionMapper Response missing(NotFoundException exception, @Context UriInfo uriInfo) {
        return error(404, "Not Found", "Contrato não encontrado.", uriInfo);
    }

    private Response error(int status, String error, String message, UriInfo uriInfo) {
        return Response.status(status).entity(
                new ApiErro(Instant.now(), status, error, message, uriInfo.getPath())).build();
    }
}
