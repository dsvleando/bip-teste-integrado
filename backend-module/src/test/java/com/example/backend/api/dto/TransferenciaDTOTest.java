package com.example.backend.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class TransferenciaDTOTest {

	@Test
	void testBuilder() {
		TransferenciaDTO dto = TransferenciaDTO.builder().fromId(1L).toId(2L).amount(new BigDecimal("50.00")).build();

		assertEquals(1L, dto.getFromId());
		assertEquals(2L, dto.getToId());
		assertEquals(new BigDecimal("50.00"), dto.getAmount());
	}

	@Test
	void testNoArgsConstructor() {
		TransferenciaDTO dto = new TransferenciaDTO();
		assertNull(dto.getFromId());
		assertNull(dto.getToId());
		assertNull(dto.getAmount());
	}

	@Test
	void testAllArgsConstructor() {
		TransferenciaDTO dto = new TransferenciaDTO(1L, 2L, new BigDecimal("50.00"));
		assertEquals(1L, dto.getFromId());
		assertEquals(2L, dto.getToId());
		assertEquals(new BigDecimal("50.00"), dto.getAmount());
	}

	@Test
	void testSettersEGetters() {
		TransferenciaDTO dto = new TransferenciaDTO();
		dto.setFromId(1L);
		dto.setToId(2L);
		dto.setAmount(new BigDecimal("100.00"));

		assertEquals(1L, dto.getFromId());
		assertEquals(2L, dto.getToId());
		assertEquals(new BigDecimal("100.00"), dto.getAmount());
	}

	@Test
	void testBuilder_Parcial() {
		TransferenciaDTO dto = TransferenciaDTO.builder().fromId(1L).build();

		assertEquals(1L, dto.getFromId());
		assertNull(dto.getToId());
		assertNull(dto.getAmount());
	}

}
