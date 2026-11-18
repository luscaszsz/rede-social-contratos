package org.caixa.exception;

public class ConteudoDaPublicacaoInalteradoException extends RuntimeException {
    public ConteudoDaPublicacaoInalteradoException() {
        super("O conteúdo informado é igual ao conteúdo atual da publicação.");
    }
}
