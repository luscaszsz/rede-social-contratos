package org.caixa.service;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.caixa.client.ContasGateway;
import org.caixa.config.PixConfig;
import org.caixa.dto.PublicacaoRequest;
import org.caixa.enums.TipoChave;
import org.caixa.exception.ChaveDuplicadaException;
import org.caixa.exception.ChaveNaoEncontradaException;
import org.caixa.model.PublicacaoModel;
import org.caixa.repository.ChavePixRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Regras de negócio do diretório de chaves.
 */
@ApplicationScoped
public class ChavePixService {

    @Inject
    ChavePixRepository repository;

    @Inject
    PixConfig config;

    @Inject
    MeterRegistry registry;

    @Inject
    ContasGateway contas;
    //ContratoGateway contrato;

    @Transactional
    public PublicacaoModel cadastrar(PublicacaoRequest request) {
        String valor = valorDaChave(request);
        exigirChaveInedita(request.tipo(), valor);
        // Regras locais primeiro; a chamada remota só acontece se elas passarem.
        //contas.confirmarConta(request.agencia(), request.numeroConta());
        PublicacaoModel publicacaoModel = new PublicacaoModel();
        publicacaoModel.tipo = request.tipo();
        publicacaoModel.valor = valor;
        publicacaoModel.numeroConta = request.numeroConta();
        publicacaoModel.titularCpf = request.titularCpf();
        publicacaoModel.criadaEm = LocalDateTime.now();
        repository.persist(publicacaoModel);
        registry.counter("pix.chaves.cadastradas", "tipo", publicacaoModel.tipo.name()).increment();
        return publicacaoModel;
    }

    public List<PublicacaoModel> listar(String titularCpf) {
        return titularCpf == null ? repository.listAll() : repository.findByTitular(titularCpf);
    }

    public PublicacaoModel buscar(Long id) {
        return repository.findByIdOptional(id).orElseThrow(() -> new ChaveNaoEncontradaException(id));
    }

    @Transactional
    public void remover(Long id) {
        if (!repository.deleteById(id)) {
            throw new ChaveNaoEncontradaException(id);
        }
    }

    private String valorDaChave(PublicacaoRequest request) {
        return request.tipo() == TipoChave.ALEATORIA ? UUID.randomUUID().toString() : request.valor();
    }

    private void exigirChaveInedita(TipoChave tipo, String valor) {
        if (repository.existeChave(tipo, valor)) {
            throw new ChaveDuplicadaException(tipo, valor);
        }
    }

    private static boolean jaEstaNaConta(PublicacaoModel chave, String agencia, String numeroConta) {
        return chave.agencia.equals(agencia) && chave.numeroConta.equals(numeroConta);
    }
}
