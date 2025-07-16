package com.github.juliherms.clienteService.controller;

import com.github.juliherms.clienteService.dto.ClienteRequestDTO;
import com.github.juliherms.clienteService.dto.ClienteResponseDTO;
import com.github.juliherms.clienteService.exception.MissingHeaderException;
import com.github.juliherms.clienteService.service.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private static final String SISTEMA_ORIGEM_HEADER = "sistemaOrigem";

    @Autowired
    private ClienteService clienteService;

    /**
     * Cadastra um novo cliente
     */
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@Valid @RequestBody ClienteRequestDTO clienteRequest) {
        logger.info("Recebida requisição para cadastrar cliente");

        ClienteResponseDTO clienteResponse = clienteService.cadastrarCliente(clienteRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(clienteResponse);
    }

    /**
     * Busca cliente por CPF - Requer header sistemaOrigem
     */
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponseDTO> buscarPorCpf(
            @PathVariable String cpf,
            @RequestHeader(value = SISTEMA_ORIGEM_HEADER, required = false) String sistemaOrigem) {

        validarSistemaOrigem(sistemaOrigem);

        logger.info("Recebida requisição para buscar cliente por CPF: {} do sistema: {}", cpf, sistemaOrigem);

        ClienteResponseDTO clienteResponse = clienteService.buscarPorCpf(cpf);

        return ResponseEntity.ok(clienteResponse);
    }

    /**
     * Busca cliente por ID - Requer header sistemaOrigem
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(
            @PathVariable Long id,
            @RequestHeader(value = SISTEMA_ORIGEM_HEADER, required = false) String sistemaOrigem) {

        validarSistemaOrigem(sistemaOrigem);

        logger.info("Recebida requisição para buscar cliente por ID: {} do sistema: {}", id, sistemaOrigem);

        ClienteResponseDTO clienteResponse = clienteService.buscarPorId(id);

        return ResponseEntity.ok(clienteResponse);
    }

    /**
     * Lista todos os clientes com paginação - Requer header sistemaOrigem
     */
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listarClientes(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestHeader(value = SISTEMA_ORIGEM_HEADER, required = false) String sistemaOrigem) {

        validarSistemaOrigem(sistemaOrigem);

        logger.info("Recebida requisição para listar clientes do sistema: {}", sistemaOrigem);

        Page<ClienteResponseDTO> clientes = clienteService.listarClientes(pageable);

        return ResponseEntity.ok(clientes);
    }

    /**
     * Busca clientes por nome - Requer header sistemaOrigem
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponseDTO>> buscarPorNome(
            @RequestParam String nome,
            @RequestHeader(value = SISTEMA_ORIGEM_HEADER, required = false) String sistemaOrigem) {

        validarSistemaOrigem(sistemaOrigem);

        logger.info("Recebida requisição para buscar clientes por nome: {} do sistema: {}", nome, sistemaOrigem);

        List<ClienteResponseDTO> clientes = clienteService.buscarPorNome(nome);

        return ResponseEntity.ok(clientes);
    }

    /**
     * Atualiza dados do cliente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO clienteRequest) {

        logger.info("Recebida requisição para atualizar cliente com ID: {}", id);

        ClienteResponseDTO clienteResponse = clienteService.atualizarCliente(id, clienteRequest);

        return ResponseEntity.ok(clienteResponse);
    }

    /**
     * Remove cliente por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerCliente(@PathVariable Long id) {
        logger.info("Recebida requisição para remover cliente com ID: {}", id);

        clienteService.removerCliente(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para verificar saúde da API
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("API Cliente está funcionando!");
    }

    /**
     * Valida se o header sistemaOrigem foi informado
     */
    private void validarSistemaOrigem(String sistemaOrigem) {
        if (sistemaOrigem == null || sistemaOrigem.trim().isEmpty()) {
            throw new MissingHeaderException("Header 'sistemaOrigem' é obrigatório para operações de consulta");
        }

        logger.debug("Sistema origem validado: {}", sistemaOrigem);
    }
}
