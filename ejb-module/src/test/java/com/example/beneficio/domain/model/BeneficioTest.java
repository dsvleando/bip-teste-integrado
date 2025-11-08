package com.example.beneficio.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class BeneficioTest {

	@Test
	void testIsAtivo_QuandoAtivo_DeveRetornarTrue() {
		Beneficio beneficio = Beneficio.builder().ativo(true).build();

		assertTrue(beneficio.isAtivo());
	}

	@Test
	void testIsAtivo_QuandoInativo_DeveRetornarFalse() {
		Beneficio beneficio = Beneficio.builder().ativo(false).build();

		assertFalse(beneficio.isAtivo());
	}

	@Test
	void testIsAtivo_QuandoNull_DeveRetornarFalse() {
		Beneficio beneficio = Beneficio.builder().ativo(null).build();

		assertFalse(beneficio.isAtivo());
	}

	@Test
	void testTemSaldoSuficiente_QuandoSaldoMaior_DeveRetornarTrue() {
		Beneficio beneficio = Beneficio.builder().valor(new BigDecimal("100.00")).build();

		assertTrue(beneficio.temSaldoSuficiente(new BigDecimal("50.00")));
	}

	@Test
	void testTemSaldoSuficiente_QuandoSaldoIgual_DeveRetornarTrue() {
		Beneficio beneficio = Beneficio.builder().valor(new BigDecimal("100.00")).build();

		assertTrue(beneficio.temSaldoSuficiente(new BigDecimal("100.00")));
	}

	@Test
	void testTemSaldoSuficiente_QuandoSaldoMenor_DeveRetornarFalse() {
		Beneficio beneficio = Beneficio.builder().valor(new BigDecimal("50.00")).build();

		assertFalse(beneficio.temSaldoSuficiente(new BigDecimal("100.00")));
	}

	@Test
	void testTemSaldoSuficiente_QuandoValorNull_DeveRetornarFalse() {
		Beneficio beneficio = Beneficio.builder().valor(null).build();

		assertFalse(beneficio.temSaldoSuficiente(new BigDecimal("100.00")));
	}

	@Test
	void testDecrementar_QuandoValorExiste_DeveSubtrair() {
		Beneficio beneficio = Beneficio.builder().valor(new BigDecimal("100.00")).build();

		beneficio.decrementar(new BigDecimal("30.00"));

		assertEquals(new BigDecimal("70.00"), beneficio.getValor());
	}

	@Test
	void testDecrementar_QuandoValorNull_DeveInicializarComZero() {
		Beneficio beneficio = Beneficio.builder().valor(null).build();

		beneficio.decrementar(new BigDecimal("30.00"));

		assertEquals(new BigDecimal("-30.00"), beneficio.getValor());
	}

	@Test
	void testIncrementar_QuandoValorExiste_DeveAdicionar() {
		Beneficio beneficio = Beneficio.builder().valor(new BigDecimal("100.00")).build();

		beneficio.incrementar(new BigDecimal("30.00"));

		assertEquals(new BigDecimal("130.00"), beneficio.getValor());
	}

	@Test
	void testIncrementar_QuandoValorNull_DeveInicializarComZero() {
		Beneficio beneficio = Beneficio.builder().valor(null).build();

		beneficio.incrementar(new BigDecimal("30.00"));

		assertEquals(new BigDecimal("30.00"), beneficio.getValor());
	}

	@Test
	void testBuilder() {
		Beneficio beneficio = Beneficio.builder().id(1L).nome("Teste").descricao("Descrição")
				.valor(new BigDecimal("100.00")).ativo(true).version(1L).build();

		assertEquals(1L, beneficio.getId());
		assertEquals("Teste", beneficio.getNome());
		assertEquals("Descrição", beneficio.getDescricao());
		assertEquals(new BigDecimal("100.00"), beneficio.getValor());
		assertTrue(beneficio.getAtivo());
		assertEquals(1L, beneficio.getVersion());
	}

	@Test
	void testNoArgsConstructor() {
		Beneficio beneficio = new Beneficio();
		assertNull(beneficio.getId());
		assertNull(beneficio.getNome());
	}

	@Test
	void testAllArgsConstructor() {
		Beneficio beneficio = new Beneficio(1L, "Teste", "Desc", new BigDecimal("100"), true, 1L);
		assertEquals(1L, beneficio.getId());
		assertEquals("Teste", beneficio.getNome());
	}
}
