package com.example.backend.api.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.example.backend.api.dto.BeneficioDTO;
import com.example.beneficio.domain.model.Beneficio;

class BeneficioDTOMapperTest {

	private final BeneficioDTOMapper mapper = Mappers.getMapper(BeneficioDTOMapper.class);

	@Test
	void testToDTO() {
		Beneficio beneficio = Beneficio.builder().id(1L).nome("Teste").descricao("Descrição")
				.valor(new BigDecimal("100.00")).ativo(true).version(1L).build();

		BeneficioDTO dto = mapper.toDTO(beneficio);

		assertNotNull(dto);
		assertEquals(1L, dto.getId());
		assertEquals("Teste", dto.getNome());
		assertEquals("Descrição", dto.getDescricao());
		assertEquals(new BigDecimal("100.00"), dto.getValor());
		assertTrue(dto.getAtivo());
		assertEquals(1L, dto.getVersion());
	}

	@Test
	void testToDomain() {
		BeneficioDTO dto = BeneficioDTO.builder().id(1L).nome("Teste").descricao("Descrição")
				.valor(new BigDecimal("100.00")).ativo(true).version(1L).build();

		Beneficio beneficio = mapper.toDomain(dto);

		assertNotNull(beneficio);
		assertEquals(1L, beneficio.getId());
		assertEquals("Teste", beneficio.getNome());
		assertEquals("Descrição", beneficio.getDescricao());
		assertEquals(new BigDecimal("100.00"), beneficio.getValor());
		assertTrue(beneficio.getAtivo());
		assertEquals(1L, beneficio.getVersion());
	}

	@Test
	void testToDTO_ComValoresNull() {
		Beneficio beneficio = Beneficio.builder().id(1L).nome("Teste").descricao(null).valor(null).ativo(null)
				.version(null).build();

		BeneficioDTO dto = mapper.toDTO(beneficio);

		assertNotNull(dto);
		assertEquals(1L, dto.getId());
		assertEquals("Teste", dto.getNome());
		assertNull(dto.getDescricao());
		assertNull(dto.getValor());
		assertNull(dto.getAtivo());
		assertNull(dto.getVersion());
	}

	@Test
	void testToDomain_ComValoresNull() {
		BeneficioDTO dto = BeneficioDTO.builder().id(1L).nome("Teste").descricao(null).valor(null).ativo(null)
				.version(null).build();

		Beneficio beneficio = mapper.toDomain(dto);

		assertNotNull(beneficio);
		assertEquals(1L, beneficio.getId());
		assertEquals("Teste", beneficio.getNome());
		assertNull(beneficio.getDescricao());
		assertNull(beneficio.getValor());
		assertNull(beneficio.getAtivo());
		assertNull(beneficio.getVersion());
	}
}
