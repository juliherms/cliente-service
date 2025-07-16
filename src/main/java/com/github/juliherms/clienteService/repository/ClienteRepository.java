package com.github.juliherms.clienteService.repository;

import com.github.juliherms.clienteService.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository  extends JpaRepository<Cliente, Long> {

    /**
     * Busca cliente por CPF
     */
    Optional<Cliente> findByCpf(String cpf);

    /**
     * Verifica se existe cliente com o CPF informado
     */
    boolean existsByCpf(String cpf);

    /**
     * Busca clientes por nome (case insensitive)
     */
    @Query("SELECT c FROM Cliente c WHERE UPPER(c.nome) LIKE UPPER(CONCAT('%', :nome, '%'))")
    List<Cliente> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    /**
     * Busca clientes por faixa de score de crédito
     */
    List<Cliente> findByScoreCreditoBetween(Integer scoreMin, Integer scoreMax);

    /**
     * Busca clientes aposentados
     */
    List<Cliente> findByAposentado(Boolean aposentado);

    /**
     * Busca clientes por profissão
     */
    List<Cliente> findByProfissaoContainingIgnoreCase(String profissao);
}


