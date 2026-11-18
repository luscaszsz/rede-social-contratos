package org.caixa.dto;

import java.time.Instant;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.caixa.model.Publicacao;

public record PublicacaoResponse(@JsonProperty("id") Long id,
                                 @JsonProperty("conteudo") String conteudo,
                                 @JsonProperty("criadaEm") Instant criadaEm,
                                 @JsonProperty("dataInicio") LocalDate dataInicio,
                                 @JsonProperty("dataFim") LocalDate dataFim,
                                 @JsonProperty("contrato") ResumoContrato contrato) {
    public static PublicacaoResponse de(Publicacao publicacao) {
        ResumoContrato contrato = publicacao.idContrato == null ? null : new ResumoContrato(publicacao.idContrato, publicacao.tituloContrato, publicacao.empresaContrato, publicacao.situacaoContrato, null, null, publicacao.nomeUsuario);
        return new PublicacaoResponse(publicacao.id, publicacao.conteudo, publicacao.criadaEm,
                publicacao.dataInicio, publicacao.dataFim, contrato);
    }
}
