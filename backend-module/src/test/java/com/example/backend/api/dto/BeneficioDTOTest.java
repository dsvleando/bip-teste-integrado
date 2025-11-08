package com.example.backend.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class BeneficioDTOTest {

	@Test
	void testBuilder() {
		BeneficioDTO dto = BeneficioDTO.builder().id(1L).nome("Teste").descricao("Descrição")
				.valor(new BigDecimal("100.00")).ativo(true).version(1L).build();

		assertEquals(1L, dto.getId());
		assertEquals("Teste", dto.getNome());
		assertEquals("Descrição", dto.getDescricao());
		assertEquals(new BigDecimal("100.00"), dto.getValor());
		assertTrue(dto.getAtivo());
		assertEquals(1L, dto.getVersion());
	}

	@Test
	void testNoArgsConstructor() {
		BeneficioDTO dto = new BeneficioDTO();
		assertNull(dto.getId());
		assertNull(dto.getNome());
	}

	@Test
	void testAllArgsConstructor() {
		BeneficioDTO dto = new BeneficioDTO(1L, "Teste", "Desc", new BigDecimal("100"), true, 1L);
		assertEquals(1L, dto.getId());
		assertEquals("Teste", dto.getNome());
	}

	@Test
	void testSettersEGetters() {
		BeneficioDTO dto = new BeneficioDTO();
		dto.setId(1L);
		dto.setNome("Teste");
		dto.setDescricao("Descrição");
		dto.setValor(new BigDecimal("100.00"));
		dto.setAtivo(false);
		dto.setVersion(2L);

		assertEquals(1L, dto.getId());
		assertEquals("Teste", dto.getNome());
		assertEquals("Descrição", dto.getDescricao());
		assertEquals(new BigDecimal("100.00"), dto.getValor());
		assertFalse(dto.getAtivo());
		assertEquals(2L, dto.getVersion());
	}

	@Test
	void testBuilder_ComTodosOsCampos() {
		BeneficioDTO dto = BeneficioDTO.builder().id(1L).nome("Teste").descricao("Descrição")
				.valor(new BigDecimal("100.00")).ativo(true).version(1L).build();

		assertEquals(1L, dto.getId());
		assertEquals("Teste", dto.getNome());
		assertEquals("Descrição", dto.getDescricao());
		assertEquals(new BigDecimal("100.00"), dto.getValor());
		assertTrue(dto.getAtivo());
		assertEquals(1L, dto.getVersion());
	}

	@Test
	void testBuilder_Parcial() {
		BeneficioDTO dto = BeneficioDTO.builder().nome("Teste").valor(new BigDecimal("50.00")).build();

		assertNull(dto.getId());
		assertEquals("Teste", dto.getNome());
		assertNull(dto.getDescricao());
		assertEquals(new BigDecimal("50.00"), dto.getValor());
		assertNull(dto.getAtivo());
		assertNull(dto.getVersion());
	}

}
