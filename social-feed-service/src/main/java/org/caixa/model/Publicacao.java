package org.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.Instant;

@Entity
public class Publicacao extends PanacheEntity {
    @Column(name = "conteudo", nullable = false, length = 1000)
    public String conteudo;

    @Column(name = "criadaEm", nullable = false)
    public Instant criadaEm;

    @Column(name = "idContrato")
    public Long idContrato;

    @Column(name = "tituloContrato")
    public String tituloContrato;

    @Column(name = "empresaContrato")
    public String empresaContrato;

    @Column(name = "situacaoContrato")
    public String situacaoContrato;

    @Column(name = "nomeUsuario", nullable = false)
    public String nomeUsuario;
}
