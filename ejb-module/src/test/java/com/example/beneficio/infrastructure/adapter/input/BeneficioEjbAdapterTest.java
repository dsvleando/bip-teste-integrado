package com.example.beneficio.infrastructure.adapter.input;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.beneficio.domain.port.input.BeneficioUseCase;

@ExtendWith(MockitoExtension.class)
class BeneficioEjbAdapterTest {

	@Mock
	private BeneficioUseCase beneficioUseCase;

	private BeneficioEjbAdapter adapter;

	@BeforeEach
	void setUp() {
		adapter = new BeneficioEjbAdapter();
	}

	@Test
	void testTransfer_Sucesso() {
		adapter.setBeneficioUseCase(beneficioUseCase);
		doNothing().when(beneficioUseCase).transferir(1L, 2L, new BigDecimal("50.00"));

		adapter.transfer(1L, 2L, new BigDecimal("50.00"));

		verify(beneficioUseCase).transferir(1L, 2L, new BigDecimal("50.00"));
	}

	@Test
	void testTransfer_QuandoUseCaseNaoConfigurado() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
			adapter.transfer(1L, 2L, new BigDecimal("50.00"));
		});

		assertTrue(exception.getMessage().contains("não foi configurado"));
		verify(beneficioUseCase, never()).transferir(anyLong(), anyLong(), any());
	}
}
