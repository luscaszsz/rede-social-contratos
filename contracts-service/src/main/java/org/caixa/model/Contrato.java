package org.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import org.caixa.enums.ContratoStatus;

@Entity
public class Contrato extends PanacheEntity {
    @Column(name = "title", nullable = false)
    public String titulo;
    @Column(name = "company", nullable = false)
    public String empresa;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public ContratoStatus situacao;
    @Column(name = "startDate", nullable = false)
    public LocalDate dataInicio;
    @Column(name = "endDate")
    public LocalDate dataFim;
}
