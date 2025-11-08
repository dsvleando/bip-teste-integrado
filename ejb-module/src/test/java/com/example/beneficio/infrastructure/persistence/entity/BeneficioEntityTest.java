package com.example.beneficio.infrastructure.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class BeneficioEntityTest {

	@Test
	void testBuilder() {
		BeneficioEntity entity = BeneficioEntity.builder().id(1L).nome("Teste").descricao("Descrição")
				.valor(new BigDecimal("100.00")).ativo(true).version(1L).build();

		assertEquals(1L, entity.getId());
		assertEquals("Teste", entity.getNome());
		assertEquals("Descrição", entity.getDescricao());
		assertEquals(new BigDecimal("100.00"), entity.getValor());
		assertTrue(entity.getAtivo());
		assertEquals(1L, entity.getVersion());
	}

	@Test
	void testBuilder_ComValorPadraoAtivo() {
		BeneficioEntity entity = BeneficioEntity.builder().id(1L).nome("Teste").build();

		assertTrue(entity.getAtivo());
	}

	@Test
	void testNoArgsConstructor() {
		BeneficioEntity entity = new BeneficioEntity();
		assertNull(entity.getId());
		assertNull(entity.getNome());
		assertNull(entity.getDescricao());
		assertNull(entity.getValor());
		assertTrue(entity.getAtivo());
		assertNull(entity.getVersion());
	}

	@Test
	void testAllArgsConstructor() {
		BeneficioEntity entity = new BeneficioEntity(1L, "Teste", "Desc", new BigDecimal("100"), true, 1L);
		assertEquals(1L, entity.getId());
		assertEquals("Teste", entity.getNome());
		assertEquals("Desc", entity.getDescricao());
		assertEquals(new BigDecimal("100"), entity.getValor());
		assertTrue(entity.getAtivo());
		assertEquals(1L, entity.getVersion());
	}

	@Test
	void testSettersEGetters() {
		BeneficioEntity entity = new BeneficioEntity();

		entity.setId(1L);
		entity.setNome("Teste");
		entity.setDescricao("Descrição");
		entity.setValor(new BigDecimal("100.00"));
		entity.setAtivo(false);
		entity.setVersion(2L);

		assertEquals(1L, entity.getId());
		assertEquals("Teste", entity.getNome());
		assertEquals("Descrição", entity.getDescricao());
		assertEquals(new BigDecimal("100.00"), entity.getValor());
		assertFalse(entity.getAtivo());
		assertEquals(2L, entity.getVersion());
	}
}
