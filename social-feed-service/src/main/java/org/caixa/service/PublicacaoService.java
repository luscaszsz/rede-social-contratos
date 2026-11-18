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

    // injeção de gateway
    @Inject GatewayContratos contratos;

    // configuração de quantas publicações podem ser feitas com o mesmo contrato
    @Inject FeedConfig config;

    // identidade basic auth
    @Inject SecurityIdentity identidade;

    // busca contratos
    public List<ResumoContrato> contratosDisponiveis() { return contratos.listarDisponiveis(); }

    //busca todas as publicações do feed
    public List<Publicacao> listar() { return Publicacao.listAll(); }

    // busca publicação com base em id
    public Publicacao buscar(Long id) { return Publicacao.<Publicacao>findByIdOptional(id).orElseThrow(() -> new NotFoundException("Publicação não encontrada.")); }

    //cria publicação
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

    // put de publicação
    @Transactional(rollbackOn = ConteudoDaPublicacaoInalteradoException.class)
    public Publicacao atualizar(Long id, PublicacaoRequest request) {
        Publicacao publicacao = buscar(id);
        if (publicacao.conteudo.equals(request.conteudo())) {
            throw new ConteudoDaPublicacaoInalteradoException();
        }
        aplicar(publicacao, request);
        return publicacao;
    }

    // deleta um contrato
    @Transactional
    public void remover(Long id) { buscar(id).delete(); }

    // junta os dados de request com os dados de publicação
    private void aplicar(Publicacao publicacao, PublicacaoRequest request) {
        publicacao.conteudo = request.conteudo();
        if (request.idContrato() == null) {
            publicacao.idContrato = null;
            publicacao.tituloContrato = null;
            publicacao.empresaContrato = null;
            publicacao.situacaoContrato = null;
            publicacao.dataInicio = null;
            publicacao.dataFim = null;
            return;
        }

        // busca contrato --> se de outro usuario, erro
        ResumoContrato contrato = contratos.confirmar(request.idContrato());
        if (!nomeUsuarioLogado().equals(contrato.nomeUsuario())) {
            throw new ForbiddenException("O contrato informado pertence a outro usuário.");
        }
        publicacao.idContrato = contrato.id();
        publicacao.tituloContrato = contrato.titulo();
        publicacao.empresaContrato = contrato.empresa();
        publicacao.situacaoContrato = contrato.situacao();
        publicacao.dataInicio = contrato.dataInicio();
        publicacao.dataFim = contrato.dataFim();
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
