package org.caixa.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ResumoContrato(@JsonProperty("id") Long id,
                             @JsonProperty("title") String titulo,
                             @JsonProperty("company") String empresa,
                             @JsonProperty("status") String situacao,
                             @JsonProperty("startDate") LocalDate dataInicio,
                             @JsonProperty("endDate") LocalDate dataFim,
                             @JsonProperty("user") String nomeUsuario) {
}
