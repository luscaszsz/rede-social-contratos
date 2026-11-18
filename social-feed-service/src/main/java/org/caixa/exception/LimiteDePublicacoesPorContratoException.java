package org.caixa.exception;

public class LimiteDePublicacoesPorContratoException extends RuntimeException {
    public LimiteDePublicacoesPorContratoException(Long idContrato, int limite) {
        super("O limite de " + limite + " publicações para o contrato " + idContrato + " foi atingido.");
    }
}
