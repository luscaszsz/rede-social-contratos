package org.caixa.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import java.time.Instant;
import java.util.List;
import org.caixa.config.FeedConfig;
import org.caixa.dto.PublicacaoRequest;
import org.caixa.dto.ResumoContrato;
import org.caixa.exception.ConteudoDaPublicacaoInalteradoException;
import org.caixa.exception.LimiteDePublicacoesPorContratoException;
import org.caixa.model.Publicacao;

@ApplicationScoped
public class PublicacaoService {
    @Inject GatewayContratos contratos;
    @Inject FeedConfig config;
    @Inject SecurityIdentity identidade;

    public List<ResumoContrato> contratosDisponiveis() { return contratos.listarDisponiveis(); }
    public List<Publicacao> listar() { return Publicacao.listAll(); }
    public Publicacao buscar(Long id) { return Publicacao.<Publicacao>findByIdOptional(id).orElseThrow(() -> new NotFoundException("Publicação não encontrada.")); }

    @Transactional
    public Publicacao criar(PublicacaoRequest request) {
        exigirLimitePorContrato(request.idContrato());
        Publicacao publicacao = new Publicacao();
        publicacao.criadaEm = Instant.now();
        publicacao.nomeUsuario = nomeUsuarioLogado();
        aplicar(publicacao, request);
        publicacao.persist();
        return publicacao;
    }

    @Transactional(rollbackOn = ConteudoDaPublicacaoInalteradoException.class)
    public Publicacao atualizar(Long id, PublicacaoRequest request) {
        Publicacao publicacao = buscar(id);
        if (publicacao.conteudo.equals(request.conteudo())) {
            throw new ConteudoDaPublicacaoInalteradoException();
        }
        aplicar(publicacao, request);
        return publicacao;
    }

    @Transactional
    public void remover(Long id) { buscar(id).delete(); }

    private void aplicar(Publicacao publicacao, PublicacaoRequest request) {
        publicacao.conteudo = request.conteudo();
        if (request.idContrato() == null) {
            publicacao.idContrato = null;
            publicacao.tituloContrato = null;
            publicacao.empresaContrato = null;
            publicacao.situacaoContrato = null;
            return;
        }
        ResumoContrato contrato = contratos.confirmar(request.idContrato());
        if (!nomeUsuarioLogado().equals(contrato.nomeUsuario())) {
            throw new ForbiddenException("O contrato informado pertence a outro usuário.");
        }
        publicacao.idContrato = contrato.id();
        publicacao.tituloContrato = contrato.titulo();
        publicacao.empresaContrato = contrato.empresa();
        publicacao.situacaoContrato = contrato.situacao();
    }

    private void exigirLimitePorContrato(Long idContrato) {
        if (idContrato == null) {
            return;
        }
        int limite = config.publicacoes().limitePorContrato();
        long quantidade = Publicacao.count("idContrato = ?1 and nomeUsuario = ?2", idContrato, nomeUsuarioLogado());
        if (quantidade >= limite) {
            throw new LimiteDePublicacoesPorContratoException(idContrato, limite);
        }
    }

    private String nomeUsuarioLogado() {
        return identidade.getPrincipal().getName();
    }
}
