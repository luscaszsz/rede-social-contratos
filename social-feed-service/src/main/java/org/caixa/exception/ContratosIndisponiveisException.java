package org.caixa.exception;

public class ContratosIndisponiveisException extends RuntimeException {
    public ContratosIndisponiveisException() {
        super("O serviço de contratos está indisponível.");
    }
}
