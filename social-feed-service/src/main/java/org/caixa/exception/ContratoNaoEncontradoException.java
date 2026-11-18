package org.caixa.exception;

public class ContratoNaoEncontradoException extends RuntimeException {
    public ContratoNaoEncontradoException() {
        super("O contrato informado não foi encontrado.");
    }
}
