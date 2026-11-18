package org.caixa.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "feed")
public interface FeedConfig {
    Publicacoes publicacoes();

    interface Publicacoes {
        int limitePorContrato();
    }
}
