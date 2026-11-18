package org.caixa.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.time.Instant;
import java.util.List;
import org.caixa.dto.PublicacaoRequest;
import org.caixa.dto.ResumoContrato;
import org.caixa.model.Publicacao;

@ApplicationScoped
public class PublicacaoService {
    @Inject GatewayContratos contratos;

    public List<ResumoContrato> contratosDisponiveis() { return contratos.listarDisponiveis(); }
    public List<Publicacao> listar() { return Publicacao.listAll(); }
    public Publicacao buscar(Long id) { return Publicacao.<Publicacao>findByIdOptional(id).orElseThrow(() -> new NotFoundException("Publicação não encontrada.")); }

    @Transactional
    public Publicacao criar(PublicacaoRequest request) {
        Publicacao publicacao = new Publicacao();
        publicacao.criadaEm = Instant.now();
        aplicar(publicacao, request);
        publicacao.persist();
        return publicacao;
    }

    @Transactional
    public Publicacao atualizar(Long id, PublicacaoRequest request) {
        Publicacao publicacao = buscar(id);
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
        publicacao.idContrato = contrato.id();
        publicacao.tituloContrato = contrato.titulo();
        publicacao.empresaContrato = contrato.empresa();
        publicacao.situacaoContrato = contrato.situacao();
    }
}
