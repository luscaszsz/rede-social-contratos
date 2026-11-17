package org.caixa.dto;

/**
 * Corpo do PUT /chaves/{id}: a nova conta à qual a chave passa a apontar.
 */
public record TrocaConteudoRequest(String novaMensagem) {
}
