package com.example.backend.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.api.dto.BeneficioDTO;
import com.example.backend.api.dto.PageDTO;
import com.example.backend.service.BeneficioService;
import com.example.beneficio.domain.exception.BeneficioException;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BeneficioServiceIntegrationTest {

	@Autowired
	private BeneficioService beneficioService;

	private BeneficioDTO beneficioDTO;

	@BeforeEach
	void setUp() {
		beneficioDTO = BeneficioDTO.builder().nome("Teste Integração").descricao("Descrição teste")
				.valor(new BigDecimal("1000.00")).ativo(true).build();
	}

	@Test
	void testCriarBeneficio() {
		BeneficioDTO criado = beneficioService.criar(beneficioDTO);

		assertNotNull(criado.getId());
		assertEquals("Teste Integração", criado.getNome());
		assertEquals(new BigDecimal("1000.00"), criado.getValor());
		assertTrue(criado.getAtivo());
	}

	@Test
	void testListarTodosPaginados() {
		beneficioService.criar(beneficioDTO);
		BeneficioDTO outro = BeneficioDTO.builder().nome("Outro").valor(new BigDecimal("500.00")).ativo(true).build();
		beneficioService.criar(outro);

		Pageable pageable = PageRequest.of(0, 10);
		PageDTO<BeneficioDTO> page = beneficioService.listarTodos(pageable);

		assertTrue(page.getContent().size() >= 2);
		assertTrue(page.getTotalElements() >= 2);
	}

	@Test
	void testBuscarPorId() {
		BeneficioDTO criado = beneficioService.criar(beneficioDTO);

		BeneficioDTO encontrado = beneficioService.buscarPorId(criado.getId());

		assertEquals(criado.getId(), encontrado.getId());
		assertEquals(criado.getNome(), encontrado.getNome());
	}

	@Test
	void testBuscarPorId_NaoEncontrado() {
		assertThrows(BeneficioException.class, () -> beneficioService.buscarPorId(999L));
	}

	@Test
	void testAtualizarBeneficio() {
		BeneficioDTO criado = beneficioService.criar(beneficioDTO);
		criado.setNome("Nome Atualizado");
		criado.setValor(new BigDecimal("2000.00"));
		criado.setDescricao("Nova descrição");
		criado.setAtivo(false);

		BeneficioDTO atualizado = beneficioService.atualizar(criado.getId(), criado);

		assertEquals("Nome Atualizado", atualizado.getNome());
		assertEquals(new BigDecimal("2000.00"), atualizado.getValor());
		assertEquals("Nova descrição", atualizado.getDescricao());
		assertEquals(false, atualizado.getAtivo());
	}

	@Test
	void testAtualizarBeneficio_NaoEncontrado() {
		beneficioDTO.setId(999L);
		assertThrows(BeneficioException.class, () -> beneficioService.atualizar(999L, beneficioDTO));
	}

	@Test
	void testRemoverBeneficio() {
		BeneficioDTO criado = beneficioService.criar(beneficioDTO);

		beneficioService.remover(criado.getId());

		assertThrows(BeneficioException.class, () -> beneficioService.buscarPorId(criado.getId()));
	}

	@Test
	void testRemoverBeneficio_NaoEncontrado() {
		assertThrows(BeneficioException.class, () -> beneficioService.remover(999L));
	}

	@Test
	void testCriarBeneficio_ComIdNull() {
		beneficioDTO.setId(null);
		BeneficioDTO criado = beneficioService.criar(beneficioDTO);

		assertNotNull(criado.getId());
	}

	@Test
	void testAtualizarBeneficio_PreservaVersion() {
		BeneficioDTO criado = beneficioService.criar(beneficioDTO);
		Long versionOriginal = criado.getVersion();

		criado.setNome("Atualizado");
		BeneficioDTO atualizado = beneficioService.atualizar(criado.getId(), criado);

		assertNotNull(atualizado.getVersion());
	}
}
