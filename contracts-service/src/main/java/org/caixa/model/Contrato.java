package org.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import org.caixa.enums.ContratoStatus;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"titulo", "empresa", "nomeUsuario"}))
public class Contrato extends PanacheEntity {

    @Column(name = "titulo", nullable = false)
    public String titulo;

    @Column(name = "empresa", nullable = false)
    public String empresa;

    // controle de quem pode publicar qual contrato
    @Column(name = "nomeUsuario", nullable = false)
    public String nomeUsuario;

    // possivel controle de não duplicação de publicação com base em contrato
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false)
    public ContratoStatus situacao;

    @Column(name = "dataInicio", nullable = false)
    public LocalDate dataInicio;

    @Column(name = "dataFim")
    public LocalDate dataFim;
}
