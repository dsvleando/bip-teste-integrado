package com.example.backend.api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.backend.api.dto.ErrorResponseDTO;
import com.example.beneficio.domain.exception.BeneficioException;

import jakarta.persistence.OptimisticLockException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

	@InjectMocks
	private GlobalExceptionHandler handler;

	@Test
	void testHandleBeneficioException_NotFound() {
		BeneficioException ex = new BeneficioException("Benefício não encontrado: 1");

		ResponseEntity<ErrorResponseDTO> response = handler.handleBeneficioException(ex);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
		assertTrue(response.getBody().getMessage().contains("não encontrado"));
	}

	@Test
	void testHandleBeneficioException_BadRequest() {
		BeneficioException ex = new BeneficioException("Saldo insuficiente");

		ResponseEntity<ErrorResponseDTO> response = handler.handleBeneficioException(ex);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
	}

	@Test
	void testHandleOptimisticLock() {
		OptimisticLockException ex = new OptimisticLockException();

		ResponseEntity<ErrorResponseDTO> response = handler.handleOptimisticLock(ex);

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getStatus());
		assertTrue(response.getBody().getMessage().contains("Conflito de versão"));
	}

	@Test
	void testHandleValidationErrors() {
		MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
		BindingResult bindingResult = mock(BindingResult.class);
		FieldError fieldError = new FieldError("beneficioDTO", "nome", "Nome é obrigatório");
		List<org.springframework.validation.ObjectError> errors = Arrays.asList(fieldError);

		when(ex.getBindingResult()).thenReturn(bindingResult);
		when(bindingResult.getAllErrors()).thenReturn(errors);

		ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().get("status"));
		assertTrue(response.getBody().containsKey("errors"));
	}

	@Test
	void testHandleGenericException() {
		Exception ex = new RuntimeException("Erro genérico");

		ResponseEntity<ErrorResponseDTO> response = handler.handleGenericException(ex);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
	}
}
