package com.example.beneficio.domain.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BeneficioExceptionTest {

	@Test
	void testConstrutorComMensagem() {
		String mensagem = "Erro de teste";
		BeneficioException exception = new BeneficioException(mensagem);

		assertEquals(mensagem, exception.getMessage());
		assertNull(exception.getCause());
	}

	@Test
	void testConstrutorComMensagemECausa() {
		String mensagem = "Erro de teste";
		Throwable causa = new RuntimeException("Causa original");
		BeneficioException exception = new BeneficioException(mensagem, causa);

		assertEquals(mensagem, exception.getMessage());
		assertEquals(causa, exception.getCause());
	}

	@Test
	void testHerancaDeRuntimeException() {
		BeneficioException exception = new BeneficioException("Teste");
		assertTrue(exception instanceof RuntimeException);
	}
}
