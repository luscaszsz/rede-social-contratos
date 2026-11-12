package org.caixa.dto;

import org.caixa.enums.TipoChave;

import java.time.LocalDateTime;

/**
 * Corpo do POST /chaves. Para chave ALEATORIA o cliente não envia valor: o servidor gera um UUID.
 */
public record PublicacaoRequest(
        TipoChave tipo,
        String valor,
        String mensagem,
        String numeroConta,
        String titularCpf,
        LocalDateTime momentoPublicacao) {
}
