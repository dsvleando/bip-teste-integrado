package com.example.backend.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ErrorResponseDTOTest {

	@Test
	void testBuilder() {
		LocalDateTime now = LocalDateTime.now();
		ErrorResponseDTO dto = ErrorResponseDTO.builder().timestamp(now).status(404).error("Not Found")
				.message("Recurso não encontrado").build();

		assertEquals(now, dto.getTimestamp());
		assertEquals(404, dto.getStatus());
		assertEquals("Not Found", dto.getError());
		assertEquals("Recurso não encontrado", dto.getMessage());
	}

	@Test
	void testNoArgsConstructor() {
		ErrorResponseDTO dto = new ErrorResponseDTO();
		assertNull(dto.getTimestamp());
		assertEquals(0, dto.getStatus());
		assertNull(dto.getError());
		assertNull(dto.getMessage());
	}

	@Test
	void testAllArgsConstructor() {
		LocalDateTime now = LocalDateTime.now();
		ErrorResponseDTO dto = new ErrorResponseDTO(now, 400, "Bad Request", "Erro");
		assertEquals(now, dto.getTimestamp());
		assertEquals(400, dto.getStatus());
		assertEquals("Bad Request", dto.getError());
		assertEquals("Erro", dto.getMessage());
	}

	@Test
	void testSettersEGetters() {
		ErrorResponseDTO dto = new ErrorResponseDTO();
		LocalDateTime now = LocalDateTime.now();
		dto.setTimestamp(now);
		dto.setStatus(500);
		dto.setError("Internal Server Error");
		dto.setMessage("Erro interno");

		assertEquals(now, dto.getTimestamp());
		assertEquals(500, dto.getStatus());
		assertEquals("Internal Server Error", dto.getError());
		assertEquals("Erro interno", dto.getMessage());
	}

	@Test
	void testBuilder_Parcial() {
		ErrorResponseDTO dto = ErrorResponseDTO.builder().status(404).error("Not Found").build();

		assertNull(dto.getTimestamp());
		assertEquals(404, dto.getStatus());
		assertEquals("Not Found", dto.getError());
		assertNull(dto.getMessage());
	}

}
