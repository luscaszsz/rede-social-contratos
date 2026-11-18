package org.caixa.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.caixa.enums.ContratoStatus;
import org.caixa.model.Contrato;

public record ContratoResponse(@JsonProperty("id") Long id,
                               @JsonProperty("title") String titulo,
                               @JsonProperty("company") String empresa,
                               @JsonProperty("status") ContratoStatus situacao,
                               @JsonProperty("startDate") LocalDate dataInicio,
                               @JsonProperty("endDate") LocalDate dataFim,
                               @JsonProperty("user") String nomeUsuario) {

    public static ContratoResponse de(Contrato contrato) {
        return new ContratoResponse(contrato.id, contrato.titulo, contrato.empresa, contrato.situacao, contrato.dataInicio, contrato.dataFim, contrato.nomeUsuario);
    }
}
