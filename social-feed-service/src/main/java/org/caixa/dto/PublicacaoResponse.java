package org.caixa.dto;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.caixa.model.Publicacao;

public record PublicacaoResponse(@JsonProperty("id") Long id,
                                 @JsonProperty("content") String conteudo,
                                 @JsonProperty("createdAt") Instant criadaEm,
                                 @JsonProperty("contract") ResumoContrato contrato) {
    public static PublicacaoResponse de(Publicacao publicacao) {
        ResumoContrato contrato = publicacao.idContrato == null ? null : new ResumoContrato(publicacao.idContrato, publicacao.tituloContrato, publicacao.empresaContrato, publicacao.situacaoContrato, null, null);
        return new PublicacaoResponse(publicacao.id, publicacao.conteudo, publicacao.criadaEm, contrato);
    }
}
