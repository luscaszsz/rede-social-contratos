package org.caixa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import org.caixa.enums.ContratoStatus;

public record ContratoRequest(@JsonProperty("title") @NotBlank String titulo,
                              @JsonProperty("company") @NotBlank String empresa,
                              @JsonProperty("status") @NotNull ContratoStatus situacao,
                              @JsonProperty("startDate") @NotNull LocalDate dataInicio,
                              @JsonProperty("endDate") LocalDate dataFim) { }
