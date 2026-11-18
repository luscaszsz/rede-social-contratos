package org.caixa.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.caixa.config.FeedConfig;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class PublicacoesLivenessCheck implements HealthCheck {
    @Inject FeedConfig config;

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("feed-publicacoes")
                .withData("limite-por-contrato", config.publicacoes().limitePorContrato())
                .up()
                .build();
    }
}
