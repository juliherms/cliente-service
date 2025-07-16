package com.github.juliherms.clienteService.service;


import com.github.juliherms.clienteService.dto.ClienteRequestDTO;
import com.github.juliherms.clienteService.dto.ClienteResponseDTO;
import com.github.juliherms.clienteService.entity.Cliente;
import com.github.juliherms.clienteService.exception.ClienteNotFoundException;
import com.github.juliherms.clienteService.exception.DuplicateCpfException;
import com.github.juliherms.clienteService.repository.ClienteRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Cadastra um novo cliente
     */
    public ClienteResponseDTO cadastrarCliente(ClienteRequestDTO clienteRequest) {
        logger.info("Iniciando cadastro de cliente com CPF: {}", clienteRequest.cpf());

        // Verifica se já existe cliente com o CPF
        if (clienteRepository.existsByCpf(clienteRequest.cpf())) {
            throw new DuplicateCpfException("Já existe um cliente cadastrado com o CPF: " + clienteRequest.cpf());
        }

        // Converte DTO para entidade
        Cliente cliente = convertToEntity(clienteRequest);

        // Salva o cliente
        Cliente clienteSalvo = clienteRepository.save(cliente);

        logger.info("Cliente cadastrado com sucesso. ID: {}", clienteSalvo.getId());

        // Converte entidade para DTO de resposta
        return convertToResponseDTO(clienteSalvo);
    }

    /**
     * Busca cliente por CPF
     */
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorCpf(String cpf) {
        logger.info("Buscando cliente por CPF: {}", cpf);

        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado com CPF: " + cpf));

        return convertToResponseDTO(cliente);
    }

    /**
     * Busca cliente por ID
     */
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        logger.info("Buscando cliente por ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado com ID: " + id));

        return convertToResponseDTO(cliente);
    }

    /**
     * Lista todos os clientes com paginação
     */
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarClientes(Pageable pageable) {
        logger.info("Listando clientes com paginação: {}", pageable);

        Page<Cliente> clientes = clienteRepository.findAll(pageable);
        return clientes.map(this::convertToResponseDTO);
    }

    /**
     * Busca clientes por nome
     */
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> buscarPorNome(String nome) {
        logger.info("Buscando clientes por nome: {}", nome);

        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        return clientes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza dados do cliente
     */
    public ClienteResponseDTO atualizarCliente(Long id, ClienteRequestDTO clienteRequest) {
        logger.info("Atualizando cliente com ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado com ID: " + id));

        // Verifica se o CPF foi alterado e se já existe outro cliente com o novo CPF
        if (!cliente.getCpf().equals(clienteRequest.cpf()) &&
                clienteRepository.existsByCpf(clienteRequest.cpf())) {
            throw new DuplicateCpfException("Já existe um cliente cadastrado com o CPF: " + clienteRequest.cpf());
        }

        // Atualiza os dados
        updateEntityFromDTO(cliente, clienteRequest);

        Cliente clienteAtualizado = clienteRepository.save(cliente);

        logger.info("Cliente atualizado com sucesso. ID: {}", clienteAtualizado.getId());

        return convertToResponseDTO(clienteAtualizado);
    }

    /**
     * Remove cliente por ID
     */
    public void removerCliente(Long id) {
        logger.info("Removendo cliente com ID: {}", id);

        if (!clienteRepository.existsById(id)) {
            throw new ClienteNotFoundException("Cliente não encontrado com ID: " + id);
        }

        clienteRepository.deleteById(id);

        logger.info("Cliente removido com sucesso. ID: {}", id);
    }

    // Métodos auxiliares para conversão

    private Cliente convertToEntity(ClienteRequestDTO dto) {
        return new Cliente(
                dto.cpf(),
                dto.nome(),
                dto.dataNascimento(),
                dto.rendaMensal(),
                dto.scoreCredito(),
                dto.aposentado(),
                dto.profissao()
        );
    }

    private ClienteResponseDTO convertToResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getCpf(),
                cliente.getNome(),
                cliente.getDataNascimento(),
                cliente.getRendaMensal(),
                cliente.getScoreCredito(),
                cliente.getAposentado(),
                cliente.getProfissao()
        );
    }

    private void updateEntityFromDTO(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setCpf(dto.cpf());
        cliente.setNome(dto.nome());
        cliente.setDataNascimento(dto.dataNascimento());
        cliente.setRendaMensal(dto.rendaMensal());
        cliente.setScoreCredito(dto.scoreCredito());
        cliente.setAposentado(dto.aposentado());
        cliente.setProfissao(dto.profissao());
    }
}


