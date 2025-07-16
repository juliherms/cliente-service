package com.github.juliherms.clienteService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para erros da API, padronizando o formato de retorno de erros.
 */
public record ErrorResponseDTO(
        String message,
        int status,
        String error,
        String path,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,
        List<String> details
) {
    /**
     * Construtor secundário que inicializa timestamp com o momento atual e sem detalhes.
     */
    public ErrorResponseDTO(String message, int status, String error, String path) {
        this(message, status, error, path, LocalDateTime.now(), null);
    }

    /**
     * Construtor secundário que inicializa timestamp com o momento atual e aceita lista de detalhes.
     */
    public ErrorResponseDTO(String message, int status, String error, String path, List<String> details) {
        this(message, status, error, path, LocalDateTime.now(), details);
    }
}


