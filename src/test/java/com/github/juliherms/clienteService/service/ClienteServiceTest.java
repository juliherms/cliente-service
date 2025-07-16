package com.github.juliherms.clienteService.service;

import com.github.juliherms.clienteService.dto.ClienteRequestDTO;
import com.github.juliherms.clienteService.dto.ClienteResponseDTO;
import com.github.juliherms.clienteService.entity.Cliente;
import com.github.juliherms.clienteService.exception.ClienteNotFoundException;
import com.github.juliherms.clienteService.exception.DuplicateCpfException;
import com.github.juliherms.clienteService.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteRequestDTO clienteRequestDTO;
    private Cliente cliente;
    private Cliente clienteSalvo;

    @BeforeEach
    void setUp() {
        clienteRequestDTO = new ClienteRequestDTO(
                "12345678901",
                "João Silva",
                LocalDate.of(1990, 5, 15),
                new BigDecimal("5000.00"),
                750,
                false,
                "Desenvolvedor"
        );

        cliente = new Cliente(
                "12345678901",
                "João Silva",
                LocalDate.of(1990, 5, 15),
                new BigDecimal("5000.00"),
                750,
                false,
                "Desenvolvedor"
        );

        clienteSalvo = new Cliente(
                "12345678901",
                "João Silva",
                LocalDate.of(1990, 5, 15),
                new BigDecimal("5000.00"),
                750,
                false,
                "Desenvolvedor"
        );
        clienteSalvo.setId(1L);
    }

    @Test
    void deveCadastrarClienteComSucesso() {
        // Given
        when(clienteRepository.existsByCpf(anyString())).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // When
        ClienteResponseDTO resultado = clienteService.cadastrarCliente(clienteRequestDTO);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.cpf()).isEqualTo("12345678901");
        assertThat(resultado.nome()).isEqualTo("João Silva");

        verify(clienteRepository).existsByCpf("12345678901");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void naoDeveCadastrarClienteComCpfDuplicado() {
        // Given
        when(clienteRepository.existsByCpf(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clienteService.cadastrarCliente(clienteRequestDTO))
                .isInstanceOf(DuplicateCpfException.class)
                .hasMessage("Já existe um cliente cadastrado com o CPF: 12345678901");

        verify(clienteRepository).existsByCpf("12345678901");
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveBuscarClientePorCpfComSucesso() {
        // Given
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.of(clienteSalvo));

        // When
        ClienteResponseDTO resultado = clienteService.buscarPorCpf("12345678901");

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.cpf()).isEqualTo("12345678901");
        assertThat(resultado.nome()).isEqualTo("João Silva");

        verify(clienteRepository).findByCpf("12345678901");
    }

    @Test
    void naoDeveBuscarClientePorCpfInexistente() {
        // Given
        when(clienteRepository.findByCpf(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clienteService.buscarPorCpf("00000000000"))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessage("Cliente não encontrado com CPF: 00000000000");

        verify(clienteRepository).findByCpf("00000000000");
    }

    @Test
    void deveBuscarClientePorIdComSucesso() {
        // Given
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(clienteSalvo));

        // When
        ClienteResponseDTO resultado = clienteService.buscarPorId(1L);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("João Silva");

        verify(clienteRepository).findById(1L);
    }

    @Test
    void naoDeveBuscarClientePorIdInexistente() {
        // Given
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clienteService.buscarPorId(999L))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessage("Cliente não encontrado com ID: 999");

        verify(clienteRepository).findById(999L);
    }

    @Test
    void deveListarClientesComPaginacao() {
        // Given
        List<Cliente> clientes = Arrays.asList(clienteSalvo);
        Page<Cliente> pageClientes = new PageImpl<>(clientes);
        Pageable pageable = PageRequest.of(0, 10);

        when(clienteRepository.findAll(pageable)).thenReturn(pageClientes);

        // When
        Page<ClienteResponseDTO> resultado = clienteService.listarClientes(pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("João Silva");

        verify(clienteRepository).findAll(pageable);
    }

    @Test
    void deveBuscarClientesPorNome() {
        // Given
        List<Cliente> clientes = Arrays.asList(clienteSalvo);
        when(clienteRepository.findByNomeContainingIgnoreCase(anyString())).thenReturn(clientes);

        // When
        List<ClienteResponseDTO> resultado = clienteService.buscarPorNome("João");

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("João Silva");

        verify(clienteRepository).findByNomeContainingIgnoreCase("João");
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        // Given
        ClienteRequestDTO clienteAtualizado = new ClienteRequestDTO(
                "12345678901",
                "João Silva Santos",
                LocalDate.of(1990, 5, 15),
                new BigDecimal("6000.00"),
                800,
                false,
                "Desenvolvedor Senior"
        );

        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // When
        ClienteResponseDTO resultado = clienteService.atualizarCliente(1L, clienteAtualizado);

        // Then
        assertThat(resultado).isNotNull();
        verify(clienteRepository).findById(1L);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void naoDeveAtualizarClienteInexistente() {
        // Given
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clienteService.atualizarCliente(999L, clienteRequestDTO))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessage("Cliente não encontrado com ID: 999");

        verify(clienteRepository).findById(999L);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void naoDeveAtualizarClienteComCpfDuplicado() {
        // Given
        ClienteRequestDTO clienteComCpfDiferente = new ClienteRequestDTO(
                "98765432100",
                "João Silva",
                LocalDate.of(1990, 5, 15),
                new BigDecimal("5000.00"),
                750,
                false,
                "Desenvolvedor"
        );

        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(clienteSalvo));
        when(clienteRepository.existsByCpf("98765432100")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clienteService.atualizarCliente(1L, clienteComCpfDiferente))
                .isInstanceOf(DuplicateCpfException.class)
                .hasMessage("Já existe um cliente cadastrado com o CPF: 98765432100");

        verify(clienteRepository).findById(1L);
        verify(clienteRepository).existsByCpf("98765432100");
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void deveRemoverClienteComSucesso() {
        // Given
        when(clienteRepository.existsById(anyLong())).thenReturn(true);

        // When
        clienteService.removerCliente(1L);

        // Then
        verify(clienteRepository).existsById(1L);
        verify(clienteRepository).deleteById(1L);
    }

    @Test
    void naoDeveRemoverClienteInexistente() {
        // Given
        when(clienteRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> clienteService.removerCliente(999L))
                .isInstanceOf(ClienteNotFoundException.class)
                .hasMessage("Cliente não encontrado com ID: 999");

        verify(clienteRepository).existsById(999L);
        verify(clienteRepository, never()).deleteById(anyLong());
    }
}


