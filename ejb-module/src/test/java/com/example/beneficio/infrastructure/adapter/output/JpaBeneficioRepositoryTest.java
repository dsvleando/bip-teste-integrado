package com.example.beneficio.infrastructure.adapter.output;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.beneficio.domain.model.Beneficio;
import com.example.beneficio.domain.model.Page;
import com.example.beneficio.domain.model.Pageable;
import com.example.beneficio.infrastructure.mapper.BeneficioMapper;
import com.example.beneficio.infrastructure.persistence.entity.BeneficioEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

@ExtendWith(MockitoExtension.class)
class JpaBeneficioRepositoryTest {

	@Mock
	private EntityManager entityManager;

	@Mock
	private BeneficioMapper mapper;

	@Mock
	private TypedQuery<BeneficioEntity> query;

	@Mock
	private TypedQuery<Long> countQuery;

	@InjectMocks
	private JpaBeneficioRepository repository;

	private BeneficioEntity entity;
	private Beneficio domain;

	@BeforeEach
	void setUp() {
		entity = BeneficioEntity.builder().id(1L).nome("Teste").descricao("Descrição").valor(new BigDecimal("100.00"))
				.ativo(true).version(1L).build();

		domain = Beneficio.builder().id(1L).nome("Teste").descricao("Descrição").valor(new BigDecimal("100.00"))
				.ativo(true).version(1L).build();
	}

	@Test
	void testBuscarPorId_QuandoEncontrado() {
		when(entityManager.find(BeneficioEntity.class, 1L)).thenReturn(entity);
		when(mapper.toDomain(entity)).thenReturn(domain);

		Optional<Beneficio> result = repository.buscarPorId(1L);

		assertTrue(result.isPresent());
		assertEquals(domain, result.get());
		verify(entityManager).find(BeneficioEntity.class, 1L);
		verify(mapper).toDomain(entity);
	}

	@Test
	void testBuscarPorId_QuandoNaoEncontrado() {
		when(entityManager.find(BeneficioEntity.class, 1L)).thenReturn(null);

		Optional<Beneficio> result = repository.buscarPorId(1L);

		assertFalse(result.isPresent());
		verify(entityManager).find(BeneficioEntity.class, 1L);
		verify(mapper, never()).toDomain(any());
	}

	@Test
	void testBuscarPorIdComLock_QuandoEncontrado() {
		when(entityManager.find(eq(BeneficioEntity.class), eq(1L), eq(LockModeType.OPTIMISTIC))).thenReturn(entity);
		when(mapper.toDomain(entity)).thenReturn(domain);

		Optional<Beneficio> result = repository.buscarPorIdComLock(1L);

		assertTrue(result.isPresent());
		assertEquals(domain, result.get());
		verify(entityManager).find(eq(BeneficioEntity.class), eq(1L), eq(LockModeType.OPTIMISTIC));
		verify(mapper).toDomain(entity);
	}

	@Test
	void testBuscarPorIdComLock_QuandoNaoEncontrado() {
		when(entityManager.find(eq(BeneficioEntity.class), eq(1L), eq(LockModeType.OPTIMISTIC))).thenReturn(null);

		Optional<Beneficio> result = repository.buscarPorIdComLock(1L);

		assertFalse(result.isPresent());
		verify(entityManager).find(eq(BeneficioEntity.class), eq(1L), eq(LockModeType.OPTIMISTIC));
		verify(mapper, never()).toDomain(any());
	}

	@Test
	void testListarTodosPaginados() {
		BeneficioEntity entity2 = BeneficioEntity.builder().id(2L).nome("Teste2").build();
		Beneficio domain2 = Beneficio.builder().id(2L).nome("Teste2").build();
		List<BeneficioEntity> entities = Arrays.asList(entity, entity2);

		Pageable pageable = Pageable.of(0, 10);

		when(entityManager.createQuery("SELECT b FROM BeneficioEntity b", BeneficioEntity.class)).thenReturn(query);
		when(entityManager.createQuery("SELECT COUNT(b) FROM BeneficioEntity b", Long.class)).thenReturn(countQuery);
		when(query.setFirstResult(0)).thenReturn(query);
		when(query.setMaxResults(10)).thenReturn(query);
		when(query.getResultList()).thenReturn(entities);
		when(countQuery.getSingleResult()).thenReturn(2L);
		when(mapper.toDomain(entity)).thenReturn(domain);
		when(mapper.toDomain(entity2)).thenReturn(domain2);

		Page<Beneficio> result = repository.listarTodos(pageable);

		assertEquals(2, result.getContent().size());
		assertEquals(0, result.getPage());
		assertEquals(10, result.getSize());
		assertEquals(2, result.getTotalElements());
		assertEquals(1, result.getTotalPages());
		assertTrue(result.isFirst());
		assertTrue(result.isLast());
		verify(entityManager).createQuery("SELECT b FROM BeneficioEntity b", BeneficioEntity.class);
		verify(entityManager).createQuery("SELECT COUNT(b) FROM BeneficioEntity b", Long.class);
		verify(query).setFirstResult(0);
		verify(query).setMaxResults(10);
		verify(mapper, times(2)).toDomain(any(BeneficioEntity.class));
	}

	@Test
	void testSalvar_Novo() {
		Beneficio novo = Beneficio.builder().nome("Novo").valor(new BigDecimal("50.00")).build();
		BeneficioEntity entityNovo = BeneficioEntity.builder().nome("Novo").valor(new BigDecimal("50.00")).build();
		BeneficioEntity entitySalvo = BeneficioEntity.builder().id(1L).nome("Novo").valor(new BigDecimal("50.00"))
				.build();
		Beneficio domainSalvo = Beneficio.builder().id(1L).nome("Novo").valor(new BigDecimal("50.00")).build();

		when(mapper.toEntity(novo)).thenReturn(entityNovo);
		when(entityManager.merge(entityNovo)).thenReturn(entitySalvo);
		when(mapper.toDomain(entitySalvo)).thenReturn(domainSalvo);

		Beneficio result = repository.salvar(novo);

		assertEquals(domainSalvo, result);
		verify(mapper).toEntity(novo);
		verify(entityManager).merge(entityNovo);
		verify(mapper).toDomain(entitySalvo);
	}

	@Test
	void testSalvar_Atualizar() {
		when(mapper.toEntity(domain)).thenReturn(entity);
		when(entityManager.merge(entity)).thenReturn(entity);
		when(mapper.toDomain(entity)).thenReturn(domain);

		Beneficio result = repository.salvar(domain);

		assertEquals(domain, result);
		verify(mapper).toEntity(domain);
		verify(entityManager).merge(entity);
		verify(mapper).toDomain(entity);
	}

	@Test
	void testRemover_QuandoExiste() {
		when(entityManager.find(BeneficioEntity.class, 1L)).thenReturn(entity);

		repository.remover(1L);

		verify(entityManager).find(BeneficioEntity.class, 1L);
		verify(entityManager).remove(entity);
	}

	@Test
	void testRemover_QuandoNaoExiste() {
		when(entityManager.find(BeneficioEntity.class, 1L)).thenReturn(null);

		repository.remover(1L);

		verify(entityManager).find(BeneficioEntity.class, 1L);
		verify(entityManager, never()).remove(any());
	}
}
