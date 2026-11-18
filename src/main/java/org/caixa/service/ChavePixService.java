package org.caixa.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.caixa.client.ContasGateway;
import org.caixa.config.Papeis;
import org.caixa.config.PixConfig;
import org.caixa.dto.PublicacaoRequest;
import org.caixa.dto.TrocaConteudoRequest;
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
    SecurityIdentity identidade;

    @Inject
    ContasGateway contas;
    //ContratoGateway contrato;

    private static final String CHAVE_DE_OUTRO = "A chave pertence a outro titular";
    private static final String LISTA_DE_OUTRO = "Titular só lista as próprias chaves";

    @Transactional
    public PublicacaoModel cadastrar(PublicacaoRequest request) {
        String valor = valorDaChave(request);
        exigirChaveInedita(request.tipo(), valor);
        // Regras locais primeiro; a chamada remota só acontece se elas passarem.
        PublicacaoModel publicacaoModel = new PublicacaoModel();
        publicacaoModel.tipo = request.tipo();
        publicacaoModel.valor = valor;
        publicacaoModel.titularCpf = request.titularCpf();
        publicacaoModel.criadaEm = LocalDateTime.now();
        publicacaoModel.mensagem = request.mensagem();
        repository.persist(publicacaoModel);
        registry.counter("pix.chaves.cadastradas", "tipo", publicacaoModel.tipo.name()).increment();
        return publicacaoModel;
    }

    /**
     java
     * Titular vê só as próprias chaves: sem filtro, a lista é a dele; filtro com o
     CPF de outro titular é 403
     * (mesma regra de posse do GET por id). Atendente e auditor veem todas, com ou
     sem filtro.
     */
    public List<PublicacaoModel> listar(String titularCpf) {
        if (titularCpf != null) {
            exigirPosse(titularCpf, LISTA_DE_OUTRO);
        }
        String filtro = ehTitular() ? usuarioLogado() : titularCpf;
        return filtro == null ? repository.listAll() :
                repository.findByTitular(filtro);
    }


    // busca publicacao
    public PublicacaoModel buscar(Long id) {
        PublicacaoModel chave = repository.findByIdOptional(id).orElseThrow(() -> new
                ChaveNaoEncontradaException(id));
        exigirPosse(chave.titularCpf, CHAVE_DE_OUTRO);
        return chave;
    }

    // remover publicacao
    @Transactional
    public void remover(Long id) {
        repository.delete(buscar(id));
    }

    @Transactional
    public PublicacaoModel trocarConteudoPublicacao(Long id, TrocaConteudoRequest request) {
        PublicacaoModel publicacaoModel = buscar(id);
//        if (!jaEstaNaConta(chave, request.agencia(), request.numeroConta())) {
//            // A própria chave não conta no limite: "trocar" para a conta em que ela já está não ocupa vaga nova.
//            exigirVagaNaConta(request.agencia(), request.numeroConta());
//        }
//        contas.confirmarConta(request.agencia(), request.numeroConta());
        publicacaoModel.mensagem = request.novaMensagem();
        // Sem save(): a entidade está gerenciada e o Hibernate grava a mudança no commit.
        return publicacaoModel;
    }

    private String valorDaChave(PublicacaoRequest request) {
        return request.tipo() == TipoChave.ALEATORIA ? UUID.randomUUID().toString() : request.valor();
    }

    private void exigirChaveInedita(TipoChave tipo, String valor) {
        if (repository.existeChave(tipo, valor)) {
            throw new ChaveDuplicadaException(tipo, valor);
        }
    }

    private void exigirPosse(String titularCpf, String mensagem) {
        if (ehTitular() && !usuarioLogado().equals(titularCpf)) {
            throw new ForbiddenException(mensagem);
        }
    }
    private boolean ehTitular() {
        return identidade.hasRole(Papeis.TITULAR);
    }
    private String usuarioLogado() {
        return identidade.getPrincipal().getName();
    }

}
