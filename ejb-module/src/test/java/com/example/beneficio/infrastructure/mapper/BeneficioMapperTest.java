package com.example.beneficio.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.example.beneficio.domain.model.Beneficio;
import com.example.beneficio.infrastructure.persistence.entity.BeneficioEntity;

class BeneficioMapperTest {

	private final BeneficioMapper mapper = Mappers.getMapper(BeneficioMapper.class);

	@Test
	void testToDomain() {
		BeneficioEntity entity = BeneficioEntity.builder().id(1L).nome("Teste").descricao("Descrição")
				.valor(new BigDecimal("100.00")).ativo(true).version(1L).build();

		Beneficio domain = mapper.toDomain(entity);

		assertNotNull(domain);
		assertEquals(1L, domain.getId());
		assertEquals("Teste", domain.getNome());
		assertEquals("Descrição", domain.getDescricao());
		assertEquals(new BigDecimal("100.00"), domain.getValor());
		assertTrue(domain.getAtivo());
		assertEquals(1L, domain.getVersion());
	}

	@Test
	void testToEntity() {
		Beneficio domain = Beneficio.builder().id(1L).nome("Teste").descricao("Descrição")
				.valor(new BigDecimal("100.00")).ativo(true).version(1L).build();

		BeneficioEntity entity = mapper.toEntity(domain);

		assertNotNull(entity);
		assertEquals(1L, entity.getId());
		assertEquals("Teste", entity.getNome());
		assertEquals("Descrição", entity.getDescricao());
		assertEquals(new BigDecimal("100.00"), entity.getValor());
		assertTrue(entity.getAtivo());
		assertEquals(1L, entity.getVersion());
	}

	@Test
	void testToDomain_ComValoresNull() {
		BeneficioEntity entity = BeneficioEntity.builder().id(1L).nome("Teste").descricao(null).valor(null).ativo(null)
				.version(null).build();

		Beneficio domain = mapper.toDomain(entity);

		assertNotNull(domain);
		assertEquals(1L, domain.getId());
		assertEquals("Teste", domain.getNome());
		assertNull(domain.getDescricao());
		assertNull(domain.getValor());
		assertNull(domain.getAtivo());
		assertNull(domain.getVersion());
	}

	@Test
	void testToEntity_ComValoresNull() {
		Beneficio domain = Beneficio.builder().id(1L).nome("Teste").descricao(null).valor(null).ativo(null)
				.version(null).build();

		BeneficioEntity entity = mapper.toEntity(domain);

		assertNotNull(entity);
		assertEquals(1L, entity.getId());
		assertEquals("Teste", entity.getNome());
		assertNull(entity.getDescricao());
		assertNull(entity.getValor());
		assertNull(entity.getAtivo());
		assertNull(entity.getVersion());
	}
}
