package com.github.juliherms.clienteService.repository;


import com.github.juliherms.clienteService.entity.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ClienteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente cliente1;
    private Cliente cliente2;

    @BeforeEach
    void setUp() {
        cliente1 = new Cliente(
                "84957281092",
                "João Silva",
                LocalDate.of(1990, 5, 15),
                new BigDecimal("5000.00"),
                750,
                false,
                "Desenvolvedor"
        );

        cliente2 = new Cliente(
                "04497909085",
                "Maria Santos",
                LocalDate.of(1985, 12, 20),
                new BigDecimal("7500.00"),
                850,
                true,
                "Gerente"
        );
    }

    @Test
    void deveSalvarCliente() {
        // When
        Cliente clienteSalvo = clienteRepository.save(cliente1);

        // Then
        assertThat(clienteSalvo).isNotNull();
        assertThat(clienteSalvo.getId()).isNotNull();
        assertThat(clienteSalvo.getCpf()).isEqualTo("84957281092");
        assertThat(clienteSalvo.getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveBuscarClientePorCpf() {
        // Given
        entityManager.persistAndFlush(cliente1);

        // When
        Optional<Cliente> clienteEncontrado = clienteRepository.findByCpf("84957281092");

        // Then
        assertThat(clienteEncontrado).isPresent();
        assertThat(clienteEncontrado.get().getNome()).isEqualTo("João Silva");
    }

    @Test
    void naoDeveBuscarClientePorCpfInexistente() {
        // When
        Optional<Cliente> clienteEncontrado = clienteRepository.findByCpf("00000000000");

        // Then
        assertThat(clienteEncontrado).isEmpty();
    }

    @Test
    void deveVerificarSeExisteClientePorCpf() {
        // Given
        entityManager.persistAndFlush(cliente1);

        // When
        boolean existe = clienteRepository.existsByCpf("84957281092");
        boolean naoExiste = clienteRepository.existsByCpf("00000000000");

        // Then
        assertThat(existe).isTrue();
        assertThat(naoExiste).isFalse();
    }

    @Test
    void deveBuscarClientesPorNome() {
        // Given
        entityManager.persistAndFlush(cliente1);
        entityManager.persistAndFlush(cliente2);

        // When
        List<Cliente> clientesJoao = clienteRepository.findByNomeContainingIgnoreCase("joão");
        List<Cliente> clientesMaria = clienteRepository.findByNomeContainingIgnoreCase("MARIA");
        List<Cliente> clientesSilva = clienteRepository.findByNomeContainingIgnoreCase("silva");

        // Then
        assertThat(clientesJoao).hasSize(1);
        assertThat(clientesJoao.get(0).getNome()).isEqualTo("João Silva");

        assertThat(clientesMaria).hasSize(1);
        assertThat(clientesMaria.get(0).getNome()).isEqualTo("Maria Santos");

        assertThat(clientesSilva).hasSize(1);
        assertThat(clientesSilva.get(0).getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveBuscarClientesPorFaixaDeScore() {
        // Given
        entityManager.persistAndFlush(cliente1); // score 750
        entityManager.persistAndFlush(cliente2); // score 850

        // When
        List<Cliente> clientesScore700a800 = clienteRepository.findByScoreCreditoBetween(700, 800);
        List<Cliente> clientesScore800a900 = clienteRepository.findByScoreCreditoBetween(800, 900);
        List<Cliente> clientesScore600a700 = clienteRepository.findByScoreCreditoBetween(600, 700);

        // Then
        assertThat(clientesScore700a800).hasSize(1);
        assertThat(clientesScore700a800.get(0).getScoreCredito()).isEqualTo(750);

        assertThat(clientesScore800a900).hasSize(1);
        assertThat(clientesScore800a900.get(0).getScoreCredito()).isEqualTo(850);

        assertThat(clientesScore600a700).isEmpty();
    }

    @Test
    void deveBuscarClientesPorStatusAposentadoria() {
        // Given
        entityManager.persistAndFlush(cliente1); // não aposentado
        entityManager.persistAndFlush(cliente2); // aposentado

        // When
        List<Cliente> clientesAposentados = clienteRepository.findByAposentado(true);
        List<Cliente> clientesNaoAposentados = clienteRepository.findByAposentado(false);

        // Then
        assertThat(clientesAposentados).hasSize(1);
        assertThat(clientesAposentados.get(0).getNome()).isEqualTo("Maria Santos");

        assertThat(clientesNaoAposentados).hasSize(1);
        assertThat(clientesNaoAposentados.get(0).getNome()).isEqualTo("João Silva");
    }

    @Test
    void deveBuscarClientesPorProfissao() {
        // Given
        entityManager.persistAndFlush(cliente1);
        entityManager.persistAndFlush(cliente2);

        // When
        List<Cliente> desenvolvedores = clienteRepository.findByProfissaoContainingIgnoreCase("desenvolvedor");
        List<Cliente> gerentes = clienteRepository.findByProfissaoContainingIgnoreCase("GERENTE");

        // Then
        assertThat(desenvolvedores).hasSize(1);
        assertThat(desenvolvedores.get(0).getProfissao()).isEqualTo("Desenvolvedor");

        assertThat(gerentes).hasSize(1);
        assertThat(gerentes.get(0).getProfissao()).isEqualTo("Gerente");
    }

    @Test
    void deveRemoverCliente() {
        // Given
        Cliente clienteSalvo = entityManager.persistAndFlush(cliente1);
        Long clienteId = clienteSalvo.getId();

        // When
        clienteRepository.deleteById(clienteId);
        entityManager.flush();

        // Then
        Optional<Cliente> clienteRemovido = clienteRepository.findById(clienteId);
        assertThat(clienteRemovido).isEmpty();
    }

    @Test
    void deveAtualizarCliente() {
        // Given
        Cliente clienteSalvo = entityManager.persistAndFlush(cliente1);

        // When
        clienteSalvo.setNome("João Silva Santos");
        clienteSalvo.setRendaMensal(new BigDecimal("6000.00"));
        Cliente clienteAtualizado = clienteRepository.save(clienteSalvo);

        // Then
        assertThat(clienteAtualizado.getNome()).isEqualTo("João Silva Santos");
        assertThat(clienteAtualizado.getRendaMensal()).isEqualTo(new BigDecimal("6000.00"));
    }
}
