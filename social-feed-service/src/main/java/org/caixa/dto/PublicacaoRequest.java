package org.caixa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PublicacaoRequest(@JsonProperty("content") @NotBlank @Size(max = 1000) String conteudo,
                                @JsonProperty("contractId") Long idContrato) {
}
