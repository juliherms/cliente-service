package com.github.juliherms.clienteService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de resposta para dados de Cliente
 */
public record ClienteResponseDTO(
        Long id,
        String cpf,
        String nome,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataNascimento,
        BigDecimal rendaMensal,
        Integer scoreCredito,
        Boolean aposentado,
        String profissao
) {}

