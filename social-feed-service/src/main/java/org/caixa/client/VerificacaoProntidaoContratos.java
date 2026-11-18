package org.caixa.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Readiness
@ApplicationScoped
public class VerificacaoProntidaoContratos implements HealthCheck {
    @Inject
    @RestClient
    ClienteHealthContratos cliente;

    @Override
    public HealthCheckResponse call() {
        return verificar();
    }

    public HealthCheckResponse verificar() {
        try {
            cliente.verificarProntidao();
            return HealthCheckResponse.up("contracts-service");
        } catch (RuntimeException excecao) {
            return HealthCheckResponse.named("contracts-service").down().build();
        }
    }
}
