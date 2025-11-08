package com.example.backend.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.api.dto.BeneficioDTO;
import com.example.backend.api.dto.TransferenciaDTO;
import com.example.backend.service.BeneficioService;
import com.example.beneficio.domain.exception.BeneficioException;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransferenciaIntegrationTest {

	@Autowired
	private BeneficioService beneficioService;

	private BeneficioDTO origem;
	private BeneficioDTO destino;

	@BeforeEach
	void setUp() {
		origem = BeneficioDTO.builder().nome("Origem").descricao("Benefício origem").valor(new BigDecimal("1000.00"))
				.ativo(true).build();

		destino = BeneficioDTO.builder().nome("Destino").descricao("Benefício destino").valor(new BigDecimal("500.00"))
				.ativo(true).build();
	}

	@Test
	void testTransferencia_Sucesso() {
		BeneficioDTO origemCriada = beneficioService.criar(origem);
		BeneficioDTO destinoCriado = beneficioService.criar(destino);

		TransferenciaDTO transferencia = TransferenciaDTO.builder().fromId(origemCriada.getId())
				.toId(destinoCriado.getId()).amount(new BigDecimal("200.00")).build();

		beneficioService.transferir(transferencia);

		BeneficioDTO origemAtualizada = beneficioService.buscarPorId(origemCriada.getId());
		BeneficioDTO destinoAtualizado = beneficioService.buscarPorId(destinoCriado.getId());

		assertEquals(new BigDecimal("800.00"), origemAtualizada.getValor());
		assertEquals(new BigDecimal("700.00"), destinoAtualizado.getValor());
	}

	@Test
	void testTransferencia_SaldoExato() {
		BeneficioDTO origemCriada = beneficioService.criar(origem);
		BeneficioDTO destinoCriado = beneficioService.criar(destino);

		TransferenciaDTO transferencia = TransferenciaDTO.builder().fromId(origemCriada.getId())
				.toId(destinoCriado.getId()).amount(new BigDecimal("1000.00")).build();

		beneficioService.transferir(transferencia);

		BeneficioDTO origemAtualizada = beneficioService.buscarPorId(origemCriada.getId());
		BeneficioDTO destinoAtualizado = beneficioService.buscarPorId(destinoCriado.getId());

		assertEquals(0, origemAtualizada.getValor().compareTo(BigDecimal.ZERO));
		assertEquals(0, destinoAtualizado.getValor().compareTo(new BigDecimal("1500.00")));
	}

	@Test
	void testTransferencia_SaldoInsuficiente() {
		BeneficioDTO origemCriada = beneficioService.criar(origem);
		BeneficioDTO destinoCriado = beneficioService.criar(destino);

		TransferenciaDTO transferencia = TransferenciaDTO.builder().fromId(origemCriada.getId())
				.toId(destinoCriado.getId()).amount(new BigDecimal("1500.00")).build();

		assertThrows(BeneficioException.class, () -> beneficioService.transferir(transferencia));
	}

	@Test
	void testTransferencia_OrigemInativa() {
		BeneficioDTO origemCriada = beneficioService.criar(origem);
		BeneficioDTO destinoCriado = beneficioService.criar(destino);

		origemCriada.setAtivo(false);
		beneficioService.atualizar(origemCriada.getId(), origemCriada);

		TransferenciaDTO transferencia = TransferenciaDTO.builder().fromId(origemCriada.getId())
				.toId(destinoCriado.getId()).amount(new BigDecimal("100.00")).build();

		assertThrows(BeneficioException.class, () -> beneficioService.transferir(transferencia));
	}

	@Test
	void testTransferencia_DestinoInativo() {
		BeneficioDTO origemCriada = beneficioService.criar(origem);
		BeneficioDTO destinoCriado = beneficioService.criar(destino);

		destinoCriado.setAtivo(false);
		beneficioService.atualizar(destinoCriado.getId(), destinoCriado);

		TransferenciaDTO transferencia = TransferenciaDTO.builder().fromId(origemCriada.getId())
				.toId(destinoCriado.getId()).amount(new BigDecimal("100.00")).build();

		assertThrows(BeneficioException.class, () -> beneficioService.transferir(transferencia));
	}

	@Test
	void testTransferencia_MesmoBeneficio() {
		BeneficioDTO beneficio = beneficioService.criar(origem);

		TransferenciaDTO transferencia = TransferenciaDTO.builder().fromId(beneficio.getId()).toId(beneficio.getId())
				.amount(new BigDecimal("100.00")).build();

		assertThrows(BeneficioException.class, () -> beneficioService.transferir(transferencia));
	}

	@Test
	void testTransferencia_ValorZero() {
		BeneficioDTO origemCriada = beneficioService.criar(origem);
		BeneficioDTO destinoCriado = beneficioService.criar(destino);

		TransferenciaDTO transferencia = TransferenciaDTO.builder().fromId(origemCriada.getId())
				.toId(destinoCriado.getId()).amount(BigDecimal.ZERO).build();

		assertThrows(BeneficioException.class, () -> beneficioService.transferir(transferencia));
	}

	@Test
	void testTransferencia_ValorNegativo() {
		BeneficioDTO origemCriada = beneficioService.criar(origem);
		BeneficioDTO destinoCriado = beneficioService.criar(destino);

		TransferenciaDTO transferencia = TransferenciaDTO.builder().fromId(origemCriada.getId())
				.toId(destinoCriado.getId()).amount(new BigDecimal("-100.00")).build();

		assertThrows(BeneficioException.class, () -> beneficioService.transferir(transferencia));
	}

	@Test
	void testTransferencia_MultiplasTransferencias() {
		BeneficioDTO origemCriada = beneficioService.criar(origem);
		BeneficioDTO destinoCriado = beneficioService.criar(destino);

		TransferenciaDTO transferencia1 = TransferenciaDTO.builder().fromId(origemCriada.getId())
				.toId(destinoCriado.getId()).amount(new BigDecimal("100.00")).build();

		TransferenciaDTO transferencia2 = TransferenciaDTO.builder().fromId(origemCriada.getId())
				.toId(destinoCriado.getId()).amount(new BigDecimal("200.00")).build();

		beneficioService.transferir(transferencia1);
		beneficioService.transferir(transferencia2);

		BeneficioDTO origemAtualizada = beneficioService.buscarPorId(origemCriada.getId());
		BeneficioDTO destinoAtualizado = beneficioService.buscarPorId(destinoCriado.getId());

		assertEquals(new BigDecimal("700.00"), origemAtualizada.getValor());
		assertEquals(new BigDecimal("800.00"), destinoAtualizado.getValor());
	}
}
