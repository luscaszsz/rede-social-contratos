package org.caixa.dto;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiErro(@JsonProperty("timestamp") Instant instante,
                      @JsonProperty("status") int codigoStatus,
                      @JsonProperty("error") String erro,
                      @JsonProperty("message") String mensagem,
                      @JsonProperty("path") String caminho) {
}
