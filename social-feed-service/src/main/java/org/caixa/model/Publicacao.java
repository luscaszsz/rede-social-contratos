package org.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.Instant;

@Entity
public class Publicacao extends PanacheEntity {
    @Column(name = "content", nullable = false, length = 1000)
    public String conteudo;
    @Column(name = "createdAt", nullable = false)
    public Instant criadaEm;
    @Column(name = "contractId") public Long idContrato;
    @Column(name = "contractTitle") public String tituloContrato;
    @Column(name = "contractCompany") public String empresaContrato;
    @Column(name = "contractStatus") public String situacaoContrato;
}
