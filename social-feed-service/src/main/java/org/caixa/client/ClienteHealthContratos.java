package org.caixa.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "contracts")
public interface ClienteHealthContratos {
    @GET
    @Path("/q/health/ready")
    String verificarProntidao();
}
