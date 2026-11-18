package org.caixa.service;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

import org.caixa.dto.ContratoRequest;
import org.caixa.dto.ContratoResponse;
import org.caixa.model.Contrato;

@ApplicationScoped
public class ContratoService {
    @Inject SecurityIdentity identidade;
    public List<ContratoResponse> listar() {
        return Contrato.<Contrato>listAll().stream().map(ContratoResponse::de).toList();
    }

    public ContratoResponse buscar(Long id) {
        return Contrato.<Contrato>findByIdOptional(id).map(ContratoResponse::de).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Contrato criar(ContratoRequest requisicao) {
        Contrato contrato = new Contrato();
        contrato.titulo = requisicao.titulo();
        contrato.empresa = requisicao.empresa();
        contrato.situacao = requisicao.situacao();
        contrato.dataInicio = requisicao.dataInicio();
        contrato.dataFim = requisicao.dataFim();
        contrato.nomeUsuario = identidade.getPrincipal().getName();
        contrato.persist();
        return contrato;
    }
}
