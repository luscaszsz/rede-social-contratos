package org.caixa.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ResumoContrato(@JsonProperty("id") Long id,
                             @JsonProperty("titulo") @JsonAlias("title") String titulo,
                             @JsonProperty("empresa") @JsonAlias("company") String empresa,
                             @JsonProperty("situacao") @JsonAlias("status") String situacao,
                             @JsonProperty("dataInicio") @JsonAlias("startDate") LocalDate dataInicio,
                             @JsonProperty("dataFim") @JsonAlias("endDate") LocalDate dataFim,
                             @JsonProperty("nomeUsuario") @JsonAlias("user") String nomeUsuario) {
}
