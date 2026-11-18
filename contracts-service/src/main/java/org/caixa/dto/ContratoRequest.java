package org.caixa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import org.caixa.enums.ContratoStatus;

public record ContratoRequest(@JsonProperty("titulo") @NotBlank String titulo,
                              @JsonProperty("empresa") @NotBlank String empresa,
                              @JsonProperty("situacao") @NotNull ContratoStatus situacao,
                              @JsonProperty("dataInicio") @NotNull LocalDate dataInicio,
                              @JsonProperty("dataFim") LocalDate dataFim,
                              @JsonProperty("responsavel") @NotNull String usuario) { }
