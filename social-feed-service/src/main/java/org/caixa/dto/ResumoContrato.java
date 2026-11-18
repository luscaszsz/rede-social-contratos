package org.caixa.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ResumoContrato(@JsonProperty("id") Long id,
                             @JsonProperty("titulo") String titulo,
                             @JsonProperty("empresa") String empresa,
                             @JsonProperty("situacao") String situacao,
                             @JsonProperty("dataInicio") LocalDate dataInicio,
                             @JsonProperty("dataFim") LocalDate dataFim,
                             @JsonProperty("nomeUsuario") String nomeUsuario) {
}
