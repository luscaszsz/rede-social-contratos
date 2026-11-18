package org.caixa.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

import org.caixa.client.ClienteContratos;
import org.caixa.dto.ResumoContrato;
import org.caixa.exception.ContratoNaoEncontradoException;
import org.caixa.exception.ContratosIndisponiveisException;

@ApplicationScoped
public class GatewayContratos {
    @Inject
    @RestClient
    ClienteContratos cliente;

    @Timeout(2000)
    @Retry(maxRetries = 2)
    @CircuitBreaker(requestVolumeThreshold = 4)
    @Fallback(fallbackMethod = "semContratos")
    public List<ResumoContrato> listarDisponiveis() {
        return cliente.listar();
    }

    @Timeout(2000)
    @Retry(maxRetries = 2, abortOn = ContratoNaoEncontradoException.class)
    @CircuitBreaker(requestVolumeThreshold = 4, skipOn = ContratoNaoEncontradoException.class)
    @Fallback(fallbackMethod = "indisponivel", skipOn = ContratoNaoEncontradoException.class)
    public ResumoContrato confirmar(Long id) {
        return cliente.buscar(id);
    }

    List<ResumoContrato> semContratos() {
        return List.of();
    }

    ResumoContrato indisponivel(Long id) {
        throw new ContratosIndisponiveisException();
    }
}
