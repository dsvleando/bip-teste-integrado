package com.example.backend.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.backend.api.dto.BeneficioDTO;
import com.example.backend.api.dto.PageDTO;
import com.example.backend.api.dto.TransferenciaDTO;
import com.example.backend.service.BeneficioService;

@ExtendWith(MockitoExtension.class)
class BeneficioControllerTest {

	@Mock
	private BeneficioService service;

	@InjectMocks
	private BeneficioController controller;

	private BeneficioDTO beneficioDTO;

	@BeforeEach
	void setUp() {
		beneficioDTO = BeneficioDTO.builder().id(1L).nome("Teste").descricao("Descrição")
				.valor(new BigDecimal("100.00")).ativo(true).version(1L).build();
	}

	@Test
	void testListarTodosPaginados() {
		BeneficioDTO dto2 = BeneficioDTO.builder().id(2L).nome("Teste2").build();
		List<BeneficioDTO> dtos = Arrays.asList(beneficioDTO, dto2);
		PageDTO<BeneficioDTO> pageDTO = PageDTO.<BeneficioDTO>builder().content(dtos).page(0).size(10).totalElements(2)
				.totalPages(1).first(true).last(true).hasNext(false).hasPrevious(false).build();
		Pageable pageable = PageRequest.of(0, 10);
		when(service.listarTodos(pageable)).thenReturn(pageDTO);

		ResponseEntity<PageDTO<BeneficioDTO>> response = controller.listarTodosPaginados(pageable);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(pageDTO, response.getBody());
		verify(service).listarTodos(pageable);
	}

	@Test
	void testBuscarPorId() {
		when(service.buscarPorId(1L)).thenReturn(beneficioDTO);

		ResponseEntity<BeneficioDTO> response = controller.buscarPorId(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(beneficioDTO, response.getBody());
		verify(service).buscarPorId(1L);
	}

	@Test
	void testCriar() {
		BeneficioDTO novoDTO = BeneficioDTO.builder().nome("Novo").valor(new BigDecimal("50.00")).build();
		BeneficioDTO criadoDTO = BeneficioDTO.builder().id(1L).nome("Novo").valor(new BigDecimal("50.00")).build();
		when(service.criar(novoDTO)).thenReturn(criadoDTO);

		ResponseEntity<BeneficioDTO> response = controller.criar(novoDTO);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(criadoDTO, response.getBody());
		verify(service).criar(novoDTO);
	}

	@Test
	void testAtualizar() {
		BeneficioDTO dtoAtualizado = BeneficioDTO.builder().nome("Atualizado").valor(new BigDecimal("200.00")).build();
		BeneficioDTO resultado = BeneficioDTO.builder().id(1L).nome("Atualizado").valor(new BigDecimal("200.00"))
				.build();
		when(service.atualizar(1L, dtoAtualizado)).thenReturn(resultado);

		ResponseEntity<BeneficioDTO> response = controller.atualizar(1L, dtoAtualizado);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(resultado, response.getBody());
		verify(service).atualizar(1L, dtoAtualizado);
	}

	@Test
	void testRemover() {
		doNothing().when(service).remover(1L);

		ResponseEntity<Void> response = controller.remover(1L);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		assertNull(response.getBody());
		verify(service).remover(1L);
	}

	@Test
	void testTransferir() {
		TransferenciaDTO dto = TransferenciaDTO.builder().fromId(1L).toId(2L).amount(new BigDecimal("50.00")).build();
		doNothing().when(service).transferir(dto);

		ResponseEntity<Void> response = controller.transferir(dto);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(service).transferir(dto);
	}
}
