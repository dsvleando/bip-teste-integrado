package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.data.domain.PageRequest;

import com.example.backend.api.dto.BeneficioDTO;
import com.example.backend.api.dto.PageDTO;
import com.example.backend.api.dto.TransferenciaDTO;
import com.example.backend.api.mapper.BeneficioDTOMapper;
import com.example.beneficio.domain.exception.BeneficioException;
import com.example.beneficio.domain.model.Beneficio;
import com.example.beneficio.domain.model.Page;
import com.example.beneficio.domain.port.input.BeneficioUseCase;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

	@Mock
	private BeneficioUseCase beneficioUseCase;

	@Mock
	private BeneficioDTOMapper mapper;

	@InjectMocks
	private BeneficioService service;

	private Beneficio beneficio;
	private BeneficioDTO beneficioDTO;

	@BeforeEach
	void setUp() {
		beneficio = Beneficio.builder().id(1L).nome("Teste").descricao("Descrição").valor(new BigDecimal("100.00"))
				.ativo(true).version(1L).build();

		beneficioDTO = BeneficioDTO.builder().id(1L).nome("Teste").descricao("Descrição")
				.valor(new BigDecimal("100.00")).ativo(true).version(1L).build();
	}

	@Test
	void testListarTodosPaginados() {
		Beneficio beneficio2 = Beneficio.builder().id(2L).nome("Teste2").build();
		BeneficioDTO dto2 = BeneficioDTO.builder().id(2L).nome("Teste2").build();
		List<Beneficio> beneficios = Arrays.asList(beneficio, beneficio2);

		Page<Beneficio> page = Page.<Beneficio>builder().content(beneficios).page(0).size(10).totalElements(2)
				.totalPages(1).first(true).last(true).build();

		org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 10);
		when(beneficioUseCase.listarTodos(any(com.example.beneficio.domain.model.Pageable.class))).thenReturn(page);
		when(mapper.toDTO(beneficio)).thenReturn(beneficioDTO);
		when(mapper.toDTO(beneficio2)).thenReturn(dto2);

		PageDTO<BeneficioDTO> result = service.listarTodos(pageable);

		assertEquals(2, result.getContent().size());
		assertEquals(0, result.getPage());
		assertEquals(10, result.getSize());
		assertEquals(2, result.getTotalElements());
		verify(beneficioUseCase).listarTodos(any(com.example.beneficio.domain.model.Pageable.class));
		verify(mapper, times(2)).toDTO(any(Beneficio.class));
	}

	@Test
	void testBuscarPorId_Sucesso() {
		when(beneficioUseCase.buscarPorId(1L)).thenReturn(Optional.of(beneficio));
		when(mapper.toDTO(beneficio)).thenReturn(beneficioDTO);

		BeneficioDTO result = service.buscarPorId(1L);

		assertEquals(beneficioDTO, result);
		verify(beneficioUseCase).buscarPorId(1L);
		verify(mapper).toDTO(beneficio);
	}

	@Test
	void testBuscarPorId_NaoEncontrado() {
		when(beneficioUseCase.buscarPorId(1L)).thenReturn(Optional.empty());

		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.buscarPorId(1L);
		});

		assertTrue(exception.getMessage().contains("não encontrado"));
		verify(beneficioUseCase).buscarPorId(1L);
		verify(mapper, never()).toDTO(any());
	}

	@Test
	void testCriar() {
		BeneficioDTO novoDTO = BeneficioDTO.builder().nome("Novo").valor(new BigDecimal("50.00")).build();
		Beneficio novo = Beneficio.builder().nome("Novo").valor(new BigDecimal("50.00")).build();
		Beneficio salvo = Beneficio.builder().id(1L).nome("Novo").valor(new BigDecimal("50.00")).build();
		BeneficioDTO salvoDTO = BeneficioDTO.builder().id(1L).nome("Novo").valor(new BigDecimal("50.00")).build();

		when(mapper.toDomain(novoDTO)).thenReturn(novo);
		when(beneficioUseCase.criar(any(Beneficio.class))).thenReturn(salvo);
		when(mapper.toDTO(salvo)).thenReturn(salvoDTO);

		BeneficioDTO result = service.criar(novoDTO);

		assertEquals(salvoDTO, result);
		verify(mapper).toDomain(novoDTO);
		verify(beneficioUseCase).criar(any(Beneficio.class));
	}

	@Test
	void testAtualizar_Sucesso() {
		BeneficioDTO dtoAtualizado = BeneficioDTO.builder().nome("Atualizado").descricao("Nova descrição")
				.valor(new BigDecimal("200.00")).ativo(false).build();
		Beneficio beneficioAtualizado = Beneficio.builder().id(1L).nome("Atualizado").descricao("Nova descrição")
				.valor(new BigDecimal("200.00")).ativo(false).build();
		BeneficioDTO dtoResult = BeneficioDTO.builder().id(1L).nome("Atualizado").descricao("Nova descrição")
				.valor(new BigDecimal("200.00")).ativo(false).build();

		when(mapper.toDomain(dtoAtualizado)).thenReturn(beneficioAtualizado);
		when(beneficioUseCase.atualizar(1L, beneficioAtualizado)).thenReturn(beneficioAtualizado);
		when(mapper.toDTO(beneficioAtualizado)).thenReturn(dtoResult);

		BeneficioDTO result = service.atualizar(1L, dtoAtualizado);

		assertEquals(dtoResult, result);
		verify(mapper).toDomain(dtoAtualizado);
		verify(beneficioUseCase).atualizar(1L, beneficioAtualizado);
	}

	@Test
	void testAtualizar_NaoEncontrado() {
		BeneficioDTO dto = BeneficioDTO.builder().nome("Teste").build();
		Beneficio beneficioDomain = Beneficio.builder().nome("Teste").build();
		when(mapper.toDomain(dto)).thenReturn(beneficioDomain);
		when(beneficioUseCase.atualizar(1L, beneficioDomain))
				.thenThrow(new BeneficioException("Benefício não encontrado: 1"));

		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.atualizar(1L, dto);
		});

		assertTrue(exception.getMessage().contains("não encontrado"));
		verify(mapper).toDomain(dto);
		verify(beneficioUseCase).atualizar(1L, beneficioDomain);
	}

	@Test
	void testRemover_Sucesso() {
		service.remover(1L);

		verify(beneficioUseCase).remover(1L);
	}

	@Test
	void testRemover_NaoEncontrado() {
		doThrow(new BeneficioException("Benefício não encontrado: 1")).when(beneficioUseCase).remover(1L);

		BeneficioException exception = assertThrows(BeneficioException.class, () -> {
			service.remover(1L);
		});

		assertTrue(exception.getMessage().contains("não encontrado"));
		verify(beneficioUseCase).remover(1L);
	}

	@Test
	void testTransferir() {
		TransferenciaDTO dto = TransferenciaDTO.builder().fromId(1L).toId(2L).amount(new BigDecimal("50.00")).build();

		service.transferir(dto);

		verify(beneficioUseCase).transferir(1L, 2L, new BigDecimal("50.00"));
	}
}
