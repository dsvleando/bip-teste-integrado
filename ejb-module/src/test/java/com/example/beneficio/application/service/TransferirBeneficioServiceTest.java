package com.example.beneficio.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.beneficio.domain.exception.BeneficioException;
import com.example.beneficio.domain.model.Beneficio;
import com.example.beneficio.domain.port.output.BeneficioRepository;

@ExtendWith(MockitoExtension.class)
class TransferirBeneficioServiceTest {

	@Mock
	private BeneficioRepository repository;

	@InjectMocks
	private BeneficioService service;

	private Beneficio origem;
	private Beneficio destino;

	@BeforeEach
	void setUp() {
		origem = Beneficio.builder().id(1L).nome("Origem").valor(new BigDecimal("100.00")).ativo(true).version(1L)
				.build();

		destino = Beneficio.builder().id(2L).nome("Destino").valor(new BigDecimal("50.00")).ativo(true).version(1L)
				.build();
	}

	@Test
	void testTransferir_Sucesso() {
		when(repository.buscarPorIdComLock(1L)).thenReturn(Optional.of(origem));
		when(repository.buscarPorIdComLock(2L)).thenReturn(Optional.of(destino));
		when(repository.salvar(any(Beneficio.class))).thenAnswer(i -> i.getArgument(0));

		service.transferir(1L, 2L, new BigDecimal("30.00"));

		verify(repository, times(2)).salvar(any(Beneficio.class));
		assertEquals(new BigDecimal("70.00"), origem.getValor());
		assertEquals(new BigDecimal("80.00"), destino.getValor());
	}

	@Test
	void testTransferir_OrigemNaoEncontrada() {
		when(repository.buscarPorIdComLock(1L)).thenReturn(Optional.empty());

		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.transferir(1L, 2L, new BigDecimal("30.00"));
		});

		assertTrue(exception.getMessage().contains("não encontrado"));
		verify(repository, never()).salvar(any());
	}

	@Test
	void testTransferir_DestinoNaoEncontrado() {
		when(repository.buscarPorIdComLock(1L)).thenReturn(Optional.of(origem));
		when(repository.buscarPorIdComLock(2L)).thenReturn(Optional.empty());

		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.transferir(1L, 2L, new BigDecimal("30.00"));
		});

		assertTrue(exception.getMessage().contains("não encontrado"));
		verify(repository, never()).salvar(any());
	}

	@Test
	void testTransferir_OrigemInativa() {
		origem.setAtivo(false);
		when(repository.buscarPorIdComLock(1L)).thenReturn(Optional.of(origem));
		when(repository.buscarPorIdComLock(2L)).thenReturn(Optional.of(destino));

		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.transferir(1L, 2L, new BigDecimal("30.00"));
		});

		assertTrue(exception.getMessage().contains("não está ativo"));
		verify(repository, never()).salvar(any());
	}

	@Test
	void testTransferir_DestinoInativo() {
		destino.setAtivo(false);
		when(repository.buscarPorIdComLock(1L)).thenReturn(Optional.of(origem));
		when(repository.buscarPorIdComLock(2L)).thenReturn(Optional.of(destino));

		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.transferir(1L, 2L, new BigDecimal("30.00"));
		});

		assertTrue(exception.getMessage().contains("não está ativo"));
		verify(repository, never()).salvar(any());
	}

	@Test
	void testTransferir_SaldoInsuficiente() {
		origem.setValor(new BigDecimal("20.00"));
		when(repository.buscarPorIdComLock(1L)).thenReturn(Optional.of(origem));
		when(repository.buscarPorIdComLock(2L)).thenReturn(Optional.of(destino));

		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.transferir(1L, 2L, new BigDecimal("30.00"));
		});

		assertTrue(exception.getMessage().contains("Saldo insuficiente"));
		verify(repository, never()).salvar(any());
	}

	@Test
	void testTransferir_FromIdNull() {
		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.transferir(null, 2L, new BigDecimal("30.00"));
		});

		assertTrue(exception.getMessage().contains("não podem ser nulos"));
	}

	@Test
	void testTransferir_ToIdNull() {
		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.transferir(1L, null, new BigDecimal("30.00"));
		});

		assertTrue(exception.getMessage().contains("não podem ser nulos"));
	}

	@Test
	void testTransferir_MesmoBeneficio() {
		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.transferir(1L, 1L, new BigDecimal("30.00"));
		});

		assertTrue(exception.getMessage().contains("mesmo benefício"));
	}

	@Test
	void testTransferir_ValorNull() {
		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.transferir(1L, 2L, null);
		});

		assertTrue(exception.getMessage().contains("maior que zero"));
	}

	@Test
	void testTransferir_ValorZero() {
		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.transferir(1L, 2L, BigDecimal.ZERO);
		});

		assertTrue(exception.getMessage().contains("maior que zero"));
	}

	@Test
	void testTransferir_ValorNegativo() {
		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.transferir(1L, 2L, new BigDecimal("-10.00"));
		});

		assertTrue(exception.getMessage().contains("maior que zero"));
	}

	@Test
	void testTransferir_OrdemInversaDosLocks() {
		when(repository.buscarPorIdComLock(2L)).thenReturn(Optional.of(destino));
		when(repository.buscarPorIdComLock(1L)).thenReturn(Optional.of(origem));
		when(repository.salvar(any(Beneficio.class))).thenAnswer(i -> i.getArgument(0));

		service.transferir(2L, 1L, new BigDecimal("30.00"));

		verify(repository, times(2)).salvar(any(Beneficio.class));
	}
}
