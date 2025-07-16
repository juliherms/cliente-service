package com.github.juliherms.clienteService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de requisição para criação/atualização de Cliente
 */
public record ClienteRequestDTO(

        @CPF(message = "CPF deve ter formato válido")
        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String nome,

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser no passado")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataNascimento,

        @NotNull(message = "Renda mensal é obrigatória")
        @DecimalMin(value = "0.0", inclusive = true, message = "Renda mensal deve ser positiva ou zero")
        @Digits(integer = 8, fraction = 2, message = "Renda mensal deve ter no máximo 8 dígitos inteiros e 2 decimais")
        BigDecimal rendaMensal,

        @NotNull(message = "Score de crédito é obrigatório")
        @Min(value = 0, message = "Score de crédito deve ser no mínimo 0")
        @Max(value = 1000, message = "Score de crédito deve ser no máximo 1000")
        Integer scoreCredito,

        @NotNull(message = "Campo aposentado é obrigatório")
        Boolean aposentado,

        @NotBlank(message = "Profissão é obrigatória")
        @Size(min = 2, max = 50, message = "Profissão deve ter entre 2 e 50 caracteres")
        String profissao

) {}
