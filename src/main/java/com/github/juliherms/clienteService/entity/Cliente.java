package com.github.juliherms.clienteService.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CPF
    @NotBlank(message = "CPF é obrigatório")
    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @NotNull(message = "Renda mensal é obrigatória")
    @PositiveOrZero(message = "Renda mensal deve ser positiva ou zero")
    @Column(name = "renda_mensal", nullable = false, precision = 10, scale = 2)
    private BigDecimal rendaMensal;

    @NotNull(message = "Score de crédito é obrigatório")
    @Column(name = "score_credito", nullable = false)
    private Integer scoreCredito;

    @Column(nullable = false)
    private Boolean aposentado;

    @NotBlank(message = "Profissão é obrigatória")
    @Column(nullable = false, length = 50)
    private String profissao;

    // Construtores
    public Cliente() {}

    public Cliente(String cpf, String nome, LocalDate dataNascimento, BigDecimal rendaMensal,
                   Integer scoreCredito, Boolean aposentado, String profissao) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.rendaMensal = rendaMensal;
        this.scoreCredito = scoreCredito;
        this.aposentado = aposentado;
        this.profissao = profissao;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public BigDecimal getRendaMensal() {
        return rendaMensal;
    }

    public void setRendaMensal(BigDecimal rendaMensal) {
        this.rendaMensal = rendaMensal;
    }

    public Integer getScoreCredito() {
        return scoreCredito;
    }

    public void setScoreCredito(Integer scoreCredito) {
        this.scoreCredito = scoreCredito;
    }

    public Boolean getAposentado() {
        return aposentado;
    }

    public void setAposentado(Boolean aposentado) {
        this.aposentado = aposentado;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id) && Objects.equals(cpf, cliente.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cpf);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", cpf='" + cpf + '\'' +
                ", nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", rendaMensal=" + rendaMensal +
                ", scoreCredito=" + scoreCredito +
                ", aposentado=" + aposentado +
                ", profissao='" + profissao + '\'' +
                '}';
    }
}
