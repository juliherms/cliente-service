package com.github.juliherms.clienteService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.juliherms.clienteService.dto.ClienteRequestDTO;
import com.github.juliherms.clienteService.dto.ClienteResponseDTO;
import com.github.juliherms.clienteService.exception.ClienteNotFoundException;
import com.github.juliherms.clienteService.exception.DuplicateCpfException;
import com.github.juliherms.clienteService.exception.MissingHeaderException;
import com.github.juliherms.clienteService.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@ActiveProfiles("test")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteRequestDTO clienteRequestDTO;
    private ClienteResponseDTO clienteResponseDTO;

    @BeforeEach
    void setUp() {
        clienteRequestDTO = new ClienteRequestDTO(
                "05960722445",
                "João Silva",
                LocalDate.of(1990, 5, 15),
                new BigDecimal("5000.00"),
                750,
                false,
                "Desenvolvedor"
        );

        clienteResponseDTO = new ClienteResponseDTO(
                1L,
                "05960722445",
                "João Silva",
                LocalDate.of(1990, 5, 15),
                new BigDecimal("5000.00"),
                750,
                false,
                "Desenvolvedor"
        );
    }

    @Test
    void deveCadastrarClienteComSucesso() throws Exception {
        // Given
        when(clienteService.cadastrarCliente(any(ClienteRequestDTO.class))).thenReturn(clienteResponseDTO);

        // When & Then
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cpf").value("05960722445"))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void naoDeveCadastrarClienteComDadosInvalidos() throws Exception {
        // Given
        ClienteRequestDTO clienteInvalido = new ClienteRequestDTO(
                "123", // CPF inválido
                "", // Nome vazio
                LocalDate.now().plusDays(1), // Data futura
                new BigDecimal("-1000"), // Renda negativa
                1500, // Score inválido
                null, // Aposentado nulo
                "" // Profissão vazia
        );

        // When & Then
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro de validação nos dados fornecidos"));
    }

    @Test
    void naoDeveCadastrarClienteComCpfDuplicado() throws Exception {
        // Given
        when(clienteService.cadastrarCliente(any(ClienteRequestDTO.class)))
                .thenThrow(new DuplicateCpfException("Já existe um cliente cadastrado com o CPF: 12345678901"));

        // When & Then
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe um cliente cadastrado com o CPF: 12345678901"));
    }

    @Test
    void deveBuscarClientePorCpfComHeaderObrigatorio() throws Exception {
        // Given
        when(clienteService.buscarPorCpf(anyString())).thenReturn(clienteResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/clientes/cpf/12345678901")
                        .header("sistemaOrigem", "SISTEMA_VENDAS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("05960722445"))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void naoDeveBuscarClientePorCpfSemHeader() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/clientes/cpf/12345678901"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Header 'sistemaOrigem' é obrigatório para operações de consulta"));
    }

    @Test
    void naoDeveBuscarClientePorCpfInexistente() throws Exception {
        // Given
        when(clienteService.buscarPorCpf(anyString()))
                .thenThrow(new ClienteNotFoundException("Cliente não encontrado com CPF: 00000000000"));

        // When & Then
        mockMvc.perform(get("/api/clientes/cpf/00000000000")
                        .header("sistemaOrigem", "SISTEMA_VENDAS"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente não encontrado com CPF: 00000000000"));
    }

    @Test
    void deveBuscarClientePorIdComHeaderObrigatorio() throws Exception {
        // Given
        when(clienteService.buscarPorId(anyLong())).thenReturn(clienteResponseDTO);

        // When & Then
        mockMvc.perform(get("/api/clientes/1")
                        .header("sistemaOrigem", "SISTEMA_VENDAS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Silva"));
    }

    @Test
    void naoDeveBuscarClientePorIdSemHeader() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Header 'sistemaOrigem' é obrigatório para operações de consulta"));
    }

    @Test
    void deveListarClientesComPaginacao() throws Exception {
        // Given
        List<ClienteResponseDTO> clientes = Arrays.asList(clienteResponseDTO);
        Page<ClienteResponseDTO> pageClientes = new PageImpl<>(clientes, PageRequest.of(0, 20), 1);
        when(clienteService.listarClientes(any())).thenReturn(pageClientes);

        // When & Then
        mockMvc.perform(get("/api/clientes")
                        .header("sistemaOrigem", "SISTEMA_VENDAS")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nome").value("João Silva"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void naoDeveListarClientesSemHeader() throws Exception {
        // When

        MvcResult result = mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Then
        Throwable resolved = result.getResolvedException();
        assertThat(resolved)
                .as("deve lançar MissingHeaderException")
                .isInstanceOf(MissingHeaderException.class);

        assertThat(resolved.getMessage())
                .isEqualTo("Header 'sistemaOrigem' é obrigatório para operações de consulta");
    }

    @Test
    void deveBuscarClientesPorNome() throws Exception {
        // Given
        List<ClienteResponseDTO> clientes = Arrays.asList(clienteResponseDTO);
        when(clienteService.buscarPorNome(anyString())).thenReturn(clientes);

        // When & Then
        mockMvc.perform(get("/api/clientes/buscar")
                        .header("sistemaOrigem", "SISTEMA_VENDAS")
                        .param("nome", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    void deveAtualizarClienteComSucesso() throws Exception {
        // Given
        ClienteResponseDTO clienteAtualizado = new ClienteResponseDTO(
                1L,
                "91267741007",
                "João Silva Santos",
                LocalDate.of(1990, 5, 15),
                new BigDecimal("6000.00"),
                800,
                false,
                "Desenvolvedor Senior"
        );

        when(clienteService.atualizarCliente(anyLong(), any(ClienteRequestDTO.class)))
                .thenReturn(clienteAtualizado);

        ClienteRequestDTO clienteRequest = new ClienteRequestDTO(
                "91267741007",
                "João Silva Santos",
                LocalDate.of(1990, 5, 15),
                new BigDecimal("6000.00"),
                800,
                false,
                "Desenvolvedor Senior"
        );

        // When & Then
        mockMvc.perform(put("/api/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva Santos"))
                .andExpect(jsonPath("$.rendaMensal").value(6000.00));
    }

    @Test
    void deveRemoverClienteComSucesso() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/clientes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarHealthCheck() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/clientes/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("API Cliente está funcionando!"));
    }
}

